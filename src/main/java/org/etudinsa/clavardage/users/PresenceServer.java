package org.etudinsa.clavardage.users;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class PresenceServer extends HttpServlet {

	private InetAddress myIP;

	public PresenceServer() {
	}

	private List<User> myDb = new ArrayList<>();

	public void init() throws ServletException {
		URL resource = this.getClass().getClassLoader().getResource("serverIP.txt");
		//System.out.println("url : " + resource.getPath());
		File file = new File(resource.getPath());
		if (file == null) {
			System.out.println("null file");
		} else {
			//System.out.println("file found");
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String st;
				while ((st = br.readLine()) != null) {
					//System.out.println(st);
					this.myIP = InetAddress.getByName(st);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("my ip " + this.myIP.getHostAddress());
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		String req = request.getParameter("request");
		if (req.equals("getDB")) {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			StringBuffer strb = new StringBuffer();
			for (User u: myDb) {
				strb.append(u.pseudo);
				strb.append(" :: ");
				strb.append(u.ip.getHostAddress());
				strb.append(" :: ");
				strb.append(Base64.getEncoder().encodeToString(u.publicKey.getEncoded()));
				out.println(strb.toString());
				strb.delete(0, strb.length());
			}
		} else {
			System.out.println("in get but not getDB");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String req = request.getParameter("request");

		if (req.equals("subscribe")) {
			String body = request.getReader().lines().collect(Collectors.joining());
			System.out.println("body post: " + body);

			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");

			String[] splitted = body.split("::", 2);
			InetAddress ip = InetAddress.getByName(request.getRemoteAddr());
			if (ip.equals(InetAddress.getLocalHost())) {
			    ip = myIP;
            }
			String ps = splitted[0].trim();
			byte[] keyBytes = Base64.getDecoder().decode(splitted[1].trim());
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			PublicKey publicKey;
			try {
				publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
				User user = new User(ps, ip, publicKey);

				if (myDb.contains(user)) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					PrintWriter out = response.getWriter();
					out.println("You already have a pseudo!");
				} else {
					boolean pseudoFound = false;
					for (User u : myDb) {
						if (u.pseudo.equals(user.pseudo)) {
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							PrintWriter out = response.getWriter();
							out.println("You can't use this pseudo!");
							pseudoFound = true;
						}
					}
					if (!pseudoFound) {
						myDb.add(user);
						PrintWriter out = response.getWriter();
						out.println("Great! You are connected!!");
					}
				}
			} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String req = request.getParameter("request");
		if (req.equals("unsubscribe")) {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			String body = request.getReader().lines().collect(Collectors.joining());
			System.out.println("body delete: " + body);

			String[] splitted = body.split("::", 2);
            InetAddress ip = InetAddress.getByName(request.getRemoteAddr());
            if (ip.equals(InetAddress.getLocalHost())) {
                ip = myIP;
            }
            String ps = splitted[0].trim();
			byte[] keyBytes = Base64.getDecoder().decode(splitted[1].trim());
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			PublicKey publicKey;
			try {
				publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
				User user = new User(ps, ip, publicKey);

				if (myDb.contains(user)) {
					myDb.remove(user);
					PrintWriter out = response.getWriter();
					out.println("User is removed from the database");
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					PrintWriter out = response.getWriter();
					out.println("User was not connected");
				}
			} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("in doPut");
		String req = request.getParameter("request");
		if (req.equals("update")) {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			String body = request.getReader().lines().collect(Collectors.joining());
			System.out.println(body);

			String[] splitted = body.split("::", 2);
            InetAddress ip = InetAddress.getByName(request.getRemoteAddr());
            if (ip.equals(InetAddress.getLocalHost())) {
                ip = myIP;
            }
            String ps = splitted[0].trim();
			byte[] keyBytes = Base64.getDecoder().decode(splitted[1].trim());
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			PublicKey publicKey;
				try {
					publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
					User user = new User(ps, ip, publicKey);
					if (myDb.contains(user)) {
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						PrintWriter out = response.getWriter();
						out.println("You already have this pseudo!");
					} else {
						boolean ipFound = false;
						for (User u : myDb) {
							System.out.println(user.ip + " " + user.publicKey + " " + u.pseudo + " " + u.publicKey);
							if (u.ip.equals(user.ip) && u.publicKey.equals(user.publicKey)) {
								myDb.remove(u);
								myDb.add(user);
								PrintWriter out = response.getWriter();
								out.println("Pseudo is updated in the database");
								ipFound = true;
							}
						}
						if (!ipFound) {
							response.setStatus(HttpServletResponse.SC_NOT_FOUND);
							PrintWriter out = response.getWriter();
							out.println("User was not in the database");
						}
					}
				} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
		}
	}

	public void destroy() {
		/* leaving empty for now this can be
		 * used when we want to do something at the end
		 * of Servlet life cycle
		 */
	}

}
