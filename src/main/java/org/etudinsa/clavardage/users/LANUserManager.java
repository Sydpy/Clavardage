package org.etudinsa.clavardage.users;

import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LANUserManager extends UserManager {

    private final static int USERDB_RETRIEVE_PORT = 9191;

    private List<User> userDB = new ArrayList<>();
    private MyUser myUser;
    private UserListener userListener;

    private boolean connected = false;

    public LANUserManager() {
    }

    @Override
    public void joinNetwork(String pseudo, KeyPair keyPair) throws Exception {

        if (connected) throw new Exception("Already connected");

        userDB = retrieveUserDB();

        User userByPseudo = getUserByPseudo(pseudo);
        if (userByPseudo != null)
            throw new Exception("Pseudo already taken.");

        myUser = new MyUser(pseudo, InetAddress.getLoopbackAddress(), keyPair);
        userDB.add(myUser);

        String pubKeyB64 = Base64.getMimeEncoder().encodeToString(myUser.publicKey.getEncoded());

        UserMessage userMsg = new UserMessage(UserMessage.Type.NEWUSER, pseudo, pubKeyB64);
        sendBroadcast(userMsg.toString().getBytes());

        startListener();

        connected = true;
    }

    @Override
    public void leaveNetwork() throws Exception {

        if (!connected) throw new Exception("Already disconnected");

        // Stop the listener
        stopListener();

        // Sign the leaving request before sending it
        String sig = myUser.signObject(UserMessage.Type.USERLEAVING);
        UserMessage userMessage = new UserMessage(UserMessage.Type.USERLEAVING, sig);
        sendBroadcast(userMessage.toString().getBytes());

        //Remove my user from the database
        synchronized (userDB) {
            userDB.remove(myUser);
        }

        connected = false;
    }

    @Override
    public void changePseudo(String pseudo) throws Exception {
    }

    /**
     * @param ip
     * @return the User instance based on the ip
     */
    @Override
    synchronized public User getUserByIp(InetAddress ip) {
        for (User user : userDB) {
            if (user.ip.equals(ip)) return user;
        }
        return null;
    }

    /**
     * @param pseudo
     * @return the User instance based on the pseudo
     */
    @Override
    synchronized public User getUserByPseudo(String pseudo) {
        for (User user : userDB) {
            if (user.pseudo.equals(pseudo)) return user;
        }
        return null;
    }

    /**
     * @return the User instance associated with this application
     */
    @Override
    synchronized public MyUser getMyUser() {
        return myUser;
    }

    @Override
    synchronized public User[] getUserDB() {
        return userDB.toArray(new User[userDB.size()]);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * @return is the application responsible of delivering the user database
     */
    synchronized public boolean isUserDBAuthority() {

        if (!connected)
            return false;

        int myUserIndex = userDB.indexOf(myUser);
        if (myUserIndex == -1) return false;

        int userDBSize = userDB.size();
        return myUserIndex == userDBSize - 1;
    }


    private void startListener() throws SocketException {
        userListener = new UserListener(this);

        Thread userListenerThread = new Thread(userListener);
        userListenerThread.start();
    }

    private void stopListener() {
        userListener.stop();
    }

    private void sendBroadcast(byte[] data) throws IOException {

        assert data.length <= 1024;

        // Create a UDP broadcast socket
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        // Broadcast it
        InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddr, UserListener.LISTENING_PORT);

        socket.send(packet);

        socket.close();
    }

    private ArrayList<User> retrieveUserDB() throws IOException {

        ArrayList<User> userDB = new ArrayList<>();
        ServerSocket serverSocket = null;

        try {
            // We first open the TCP socket on which we are going to retrieve the UserDB from de UserDBAuthority
            // We do it before broadcasting the UserDB Request to be sure that it is open when it will reply

            // We configure a timeout on the sockets to handle the case where we are alone
            serverSocket = new ServerSocket(USERDB_RETRIEVE_PORT);
            serverSocket.setSoTimeout(2000);

            // Ask all the network in hope that the UserDBAuthority answers
            UserMessage dbReq = new UserMessage(UserMessage.Type.USERDB_REQUEST);
            sendBroadcast(dbReq.toString().getBytes());

            Socket socket = serverSocket.accept();
            socket.setSoTimeout(2000);

            InputStream is = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(is);

            while (true) {
                try {
                    // Read serialized user object from the connection with the UserDB Authority
                    User user;
                    if ((user = (User) objectInputStream.readObject()) == null) break;

                    userDB.add(user);
                } catch (EOFException ef) {
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            socket.close();
            serverSocket.close();

        } catch (SocketTimeoutException ignored) {
        } finally {
            serverSocket.close();
        }

        return userDB;
    }

    private void sendUserDB(InetAddress ip) throws IOException {
        Socket socket = new Socket(ip, USERDB_RETRIEVE_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        synchronized (userDB) {
            for (User user : userDB) {

                if (user.equals(myUser))
                    user = new User(myUser.pseudo, socket.getLocalAddress(), myUser.publicKey);

                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
            }
        }

        objectOutputStream.close();
        socket.close();
    }

    void receivedMessageFrom(UserMessage message, InetAddress address) {

        switch (message.type) {

            case USERDB_REQUEST:
                if (isUserDBAuthority()) {
                    try {
                        sendUserDB(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case NEWUSER:

                if (message.content.length != 2) break;

                try {

                    String pseudo = message.content[0];

                    byte[] keyBytes = Base64.getMimeDecoder().decode(message.content[1]);
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                    PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

                    User newUser = new User(pseudo, address, publicKey);

                    synchronized (userDB) {
                        userDB.add(newUser);
                    }

                    notifyNewUser(newUser);

                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                break;
            case USERLEAVING:

                User userLeaving = getUserByIp(address);
                if (userLeaving == null)
                    break;

                if (message.content.length != 1)
                    break;

                try {
                    if (!userLeaving.verifySig(UserMessage.Type.USERLEAVING, message.content[0]))
                        break;

                    synchronized (userDB) {
                        userDB.remove(userLeaving);
                    }

                    notifyUserLeaving(userLeaving);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
