package org.etudinsa.clavardage.users;

import java.io.*;
import java.net.*;
import java.util.*;

public class UserManager extends Observable implements Observer {

    private final static int USERDB_RETRIEVE_PORT = 9191;

    private static UserManager instance = new UserManager();

    public static UserManager getInstance() {
        return instance;
    }


    private List<User> userDB = new ArrayList<>();
    private User myUser;
    private UserListener userListener;

    private boolean connected = false;

    private UserManager() {}

    public void joinNetwork(String pseudo) throws Exception {

        if (connected) throw new Exception("Already connected");

        userDB = retrieveUserDB();

        User userByPseudo = getUserByPseudo(pseudo);
        if (userByPseudo != null)
            throw new Exception("Pseudo already taken.");

        myUser = new User(pseudo, InetAddress.getLoopbackAddress());
        userDB.add(myUser);
        
        UserMessage userMsg = new UserMessage(UserMessage.Type.NEWUSER,pseudo);
        sendBroadcast(userMsg.toString().getBytes());

        startListener();

        connected = true;
    }

    public void leaveNetwork() throws Exception {

        if (!connected) throw new Exception("Already connected");
        //TODO : Broadcast USERLEAVING
        stopListener();

        connected = false;
    }
    /**
     * @param ip
     * @return the User instance based on the ip
     */
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
    synchronized public User getUserByPseudo(String pseudo) {
        for (User user : userDB) {
            if (user.pseudo.equals(pseudo)) return user;
        }
        return null;
    }

    /**
     * @return the User instance associated with this application
     */
    synchronized public User getMyUser() {
        return myUser;
    }

    synchronized public User[] getUserDB() {
        return userDB.toArray(new User[userDB.size()]);
    }

    public boolean isConnected() {
        return connected;
    }

    private void startListener() throws SocketException {
        userListener = new UserListener();

        userListener.addObserver(this);

        Thread userListenerThread = new Thread(userListener);
        userListenerThread.start();
    }

    private void stopListener() {
        userListener.stop();
    }

    /**
     * @return is the application responsible of delivering the user database
     */
    synchronized private boolean isUserDBAuthority() {

        if (!connected)
            return false;

        int myUserIndex = userDB.indexOf(myUser);
        if (myUserIndex == -1) return false;

        int userDBSize = userDB.size();
        return myUserIndex == userDBSize - 1;
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

        // Ask all the network in hope that the UserDBAuthority answers
        UserMessage dbReq = new UserMessage(UserMessage.Type.USERDB_REQUEST);
        sendBroadcast(dbReq.toString().getBytes());

        try {
            // We configure a timeout on the sockets to handle the case where we are alone
            ServerSocket serverSocket = new ServerSocket(USERDB_RETRIEVE_PORT);
            serverSocket.setSoTimeout(5000);

            Socket socket = serverSocket.accept();
            socket.setSoTimeout(5000);

            InputStream is = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(is);

            while (true) {
                try {
                    // Read serialized user object from the connection with the UserDB Authority
                    User user;
                    if ((user = (User) objectInputStream.readObject()) == null) break;

                    System.out.println("message received from user: " + user);
                    userDB.add(user);
                } catch (EOFException ef) {
                	System.out.println("eof exception");
                	break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            socket.close();
            serverSocket.close();

        } catch(SocketTimeoutException ignored) {}

        return userDB;
    }

    private void sendUserDB(InetAddress ip) throws IOException {
        Socket socket = new Socket(ip, USERDB_RETRIEVE_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        synchronized (userDB) {
            for (User user : userDB) {

                if (user.equals(myUser))
                    user = new User(myUser.pseudo, socket.getInetAddress());

                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
                System.out.println("user sent db: " + user);
            }
        }

        objectOutputStream.close();
        socket.close();
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof UserListener.ReceivedUserMessage) {

            InetAddress addr = ((UserListener.ReceivedUserMessage) o).address;
            UserMessage bm = ((UserListener.ReceivedUserMessage) o).userMessage;

            switch (bm.type) {
                case USERDB_REQUEST:
                    System.out.println("Received UserDB request.");
                    if (isUserDBAuthority()) {
                        System.out.println("Sending UserDB.");
                        try {
                            sendUserDB(addr);
                            System.out.println("Done sending.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case NEWUSER:
                    System.out.println("Received 'new user' notification.");
                    synchronized (userDB) {
                        userDB.add(new User(bm.pseudo, addr));
                    }
                    break;
                case USERLEAVING:
                    System.out.println("Received 'user leaving' notification");
                    synchronized (userDB) {
                        userDB.remove(getUserByIp(addr));
                    }
                    break;
            }
        }
    }
}
