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


    private List<User> userDB;
    private String myPseudo;

    private UserListener userListener;

    private UserManager() {}

    public void start() {
        try {
            userListener = new UserListener();

            userListener.addObserver(this);

            Thread userListenerThread = new Thread(userListener);
            userListenerThread.start();

        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void stop() {
        userListener.stop();
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
        return getUserByPseudo(myPseudo);
    }

    synchronized public User[] getUserDB() {
        return userDB.toArray(new User[userDB.size()]);
    }

    /**
     * @param pseudo pseudo to give to the application's User
     * @throws IOException if the retrieval of the User DB or the advertising of the application
     *
     * create the application's User
     */
    synchronized public void createMyUser(String pseudo) throws IOException {

        // We can create a user for the current application only if there
        // is none already created
        assert myPseudo == null;

        // Retrieve userDB from the current UserDBAuthority of the network
        retrieveUserDB();
        
        this.myPseudo = pseudo;

        
        // Advertise the fact that we are now a user of the network
        advertiseNewUser(pseudo);        
    }

    /**
     * @return is the application responsible of delivering the user database
     */
    synchronized private boolean isUserDBAuthority() {
    	
    	int index = userDB.indexOf(getMyUser());
    	
        return index != -1 && index == userDB.size() - 1;
    }

    private void sendBroadcast(BroadcastMessage mes) throws IOException {

        // Create a UDP broadcast socket
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] data = mes.toString().getBytes();

        assert data.length <= 1024;

        // Broadcast it
        InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddr, UserListener.LISTENING_PORT);

        socket.send(packet);

        socket.close();
    }

    private void advertiseNewUser(String pseudo) throws IOException {
        sendBroadcast(new BroadcastMessage(BroadcastMessage.Type.NEWUSER, pseudo));
    }

    private void retrieveUserDB() throws IOException {
    	
        userDB = new ArrayList<>();
        
        // Ask all the network in hope that the UserDBAuthority answers
        sendBroadcast(new BroadcastMessage(BroadcastMessage.Type.USERDB_REQUEST));

        try {
            // We configure a timeout on the sockets to handle the case where we are alone
            ServerSocket serverSocket = new ServerSocket(USERDB_RETRIEVE_PORT);
            serverSocket.setSoTimeout(5000);
            
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(5000);

            InputStream is = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(is);

            synchronized (userDB) {
                while (true) {
                    try {
                        // Read serialized user object from the connection with the UserDB Authority
                        User user;
                        if ((user = (User) objectInputStream.readObject()) == null) break;

                        userDB.add(user);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                if (userDB.size() > 0) {
                    //The UserDBAuthority 's user must be the last one on the list
                    //as for him his address is the loopback address, we must replace it
                    //with its known address
                    User lastUser = userDB.remove(userDB.size() - 1);

                    InetAddress userDBAuthorityAddr = socket.getInetAddress();
                    String userDBAuthorityPseudo = lastUser.pseudo;
                    User userDBAUthority = new User(userDBAuthorityPseudo, userDBAuthorityAddr);

                    userDB.add(userDBAUthority);
                }
            }

            socket.close();
            serverSocket.close();

        } catch(SocketTimeoutException ignored) {}
    }

    private void sendUserDB(InetAddress ip) throws IOException {
        Socket socket = new Socket(ip, USERDB_RETRIEVE_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        synchronized (userDB) {
            for (User user : userDB) {
                objectOutputStream.writeObject(user);
                objectOutputStream.flush();
            }
        }
        
        objectOutputStream.close();
        socket.close();
    }

    @Override
    public void update(Observable observable, Object o) {
    	
        if (o instanceof UserListener.ReceivedBroadcastMessage) {

            InetAddress addr = ((UserListener.ReceivedBroadcastMessage) o).address;
            BroadcastMessage bm = ((UserListener.ReceivedBroadcastMessage) o).broadcastMessage;
            
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
