package org.etudinsa.clavardage.users;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServerUserManager extends UserManager {

	private String serverUrl;
	private boolean connected = false;
	private List<User> userDB = new ArrayList<>();
    private MyUser myUser;
    private Timeline timeline;
	String charset = StandardCharsets.UTF_8.name();
    
	
	public ServerUserManager(InetAddress ip) {
		this.serverUrl = "http://" + ip.getHostAddress() + ":8080/ServerClavardage/clavardage";
		timeline = new Timeline(new KeyFrame(Duration.seconds(10), ev -> {
			ArrayList<User> db = retrieveUserDB();
			synchronized (userDB) {
				if (!userDB.equals(db)) {
					userDB = db;
					notifyUpdatedUserList();
				}
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	synchronized private ArrayList<User> retrieveUserDB() {
		ArrayList<User> userDB = new ArrayList<>();

		URL url;
		HttpURLConnection con;
		String charset = StandardCharsets.UTF_8.name();
		String inputLine;
		BufferedReader in;
		String query;

		try {
			query = String.format("request=%s", URLEncoder.encode("getDB", charset));
			url = new URL(this.serverUrl + "?" + query);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept-Charset", charset);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			userDB.clear();

			String splitted[];
			InetAddress ip;
			String ps;
			byte[] keyBytes;
			X509EncodedKeySpec keySpec;
			PublicKey publicKey;
			User user;

			while ((inputLine = in.readLine()) != null) {
				splitted = inputLine.split(" :: ", 3);
				ip = InetAddress.getByName(splitted[1].trim());
				ps = splitted[0].trim();
				keyBytes = Base64.getDecoder().decode(splitted[2]);
				keySpec = new X509EncodedKeySpec(keyBytes);
				publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
				user = new User(ps, ip, publicKey);
				userDB.add(user);
			}

			in.close();
			con.disconnect();

		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

//		System.out.println("get DB");
//
//		for (User ur:userDB) {
//			System.out.println(ur.toString());
//		}

		return userDB;

	}

	@Override
	public void joinNetwork(String pseudo, KeyPair keyPair) throws Exception {
		
		if (connected) throw new Exception("Already connected");
		
		URL url = new URL(serverUrl + "?" + String.format("request=%s", URLEncoder.encode("subscribe", charset)));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Accept-Charset", charset);
		con.setRequestProperty("Content-Type", "text/plain");
		
        myUser = new MyUser(pseudo, InetAddress.getLoopbackAddress(), keyPair);

		String body = myUser.pseudo + " :: " + Base64.getEncoder().encodeToString(myUser.publicKey.getEncoded());
		
		OutputStream outputStream = con.getOutputStream();
		outputStream.write(body.getBytes(charset));
		outputStream.close();
		
		BufferedReader in;
		StringBuffer contentB = new StringBuffer();
		String inputLine;
		if (con.getResponseCode() != 200) {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				contentB.append(inputLine);
			}
			in.close();
			con.disconnect();
			throw new Exception(contentB.toString());
		}
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			contentB.append(inputLine);
		}
		in.close();
		con.disconnect();
//		System.out.println("Subscribe message");
//		System.out.println(contentB.toString());
        
        connected = true;
		}

		@Override
		public void leaveNetwork() throws Exception {
			if (!connected) throw new Exception("Already disconnected");

			timeline.stop();
			
			URL url = new URL(serverUrl + "?" + String.format("request=%s", URLEncoder.encode("unsubscribe", charset)));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("DELETE");
			con.setDoOutput(true);
			con.setRequestProperty("Accept-Charset", charset);
			con.setRequestProperty("Content-Type", "text/plain");
			
			String body = myUser.pseudo + " :: " + Base64.getEncoder().encodeToString(myUser.publicKey.getEncoded());
			
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(body.getBytes(charset));
			outputStream.close();
			
			BufferedReader in ;
			StringBuffer contentB;
			String inputLine;

			if (con.getResponseCode() != 200) {
				in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				contentB = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					contentB.append(inputLine);
				}
				in.close();
				con.disconnect();
				throw new Exception(contentB.toString());
			}
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			contentB = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				contentB.append(inputLine);
			}
			in.close();
			con.disconnect();
//			System.out.println("unsubcribe message");
//			System.out.println(contentB.toString());
			
			connected = false;
		}

	@Override
	public void changePseudo(String pseudo) throws Exception {

		if (!connected) throw new Exception("User not connected");

		URL url = new URL(serverUrl + "?" + String.format("request=%s", URLEncoder.encode("update", charset)));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("PUT");
		con.setDoOutput(true);
		con.setRequestProperty("Accept-Charset", charset);
		con.setRequestProperty("Content-Type", "text/plain");

		String body = pseudo + " :: " + Base64.getEncoder().encodeToString(myUser.publicKey.getEncoded());

		OutputStream outputStream = con.getOutputStream();
		outputStream.write(body.getBytes(charset));
		outputStream.close();

		BufferedReader in;
		StringBuffer contentB = new StringBuffer();
		String inputLine;
		if (con.getResponseCode() != 200) {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			while ((inputLine = in.readLine()) != null) {
				contentB.append(inputLine);
			}
			in.close();
			con.disconnect();
			throw new Exception(contentB.toString());
		}
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			contentB.append(inputLine);
		}
		myUser.setPseudo(pseudo);
		in.close();
		con.disconnect();
	}

	@Override
		public User getUserByIp(InetAddress ip) {
			synchronized (userDB) {
				userDB = retrieveUserDB();
		        for (User user : userDB) {
		            if (user.ip.equals(ip)) return user;
		        }
			}
	        return null;
		}

		@Override
		public User getUserByPseudo(String pseudo) {
			synchronized (userDB) {
				userDB = retrieveUserDB();
		        for (User user : userDB) {
		            if (user.pseudo.equals(pseudo)) return user;
		        }
			}
	        return null;
		}

		@Override
		public MyUser getMyUser() {
			return myUser;
		}

		@Override
		public User[] getUserDB() {
			synchronized (userDB) {
				userDB = retrieveUserDB();
				return userDB.toArray(new User[userDB.size()]);
			}
		}

		@Override
		public boolean isConnected() {
			return connected;
		}

	}
