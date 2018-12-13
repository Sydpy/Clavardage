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
    private User myUser;

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
        return myUser;
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
        assert myUser == null;

        // Retrieve de userDB from the current UserDBAuthority of the network
        retrieveUserDB();

        //TODO : check if pseudo is in userDB
        myUser = new User(pseudo, InetAddress.getLoopbackAddress());

        // Advertise the fact that we are now a user of the network
        advertiseMyself();

        // Add myUser at the end of the userDB
        userDB.add(myUser);
    }

    /**
     * @return is the application responsible of delivering the user database
     */
    synchronized private boolean isUserDBAuthority() {
        return userDB.indexOf(myUser) == userDB.size() - 1;
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

    private void advertiseMyself() throws IOException {

        assert myUser != null;

        sendBroadcast(new BroadcastMessage(BroadcastMessage.Type.NEWUSER, myUser.pseudo));
    }

    private void retrieveUserDB() throws IOException {
        // Ask all the network in hope that the UserDBAuthority answers
        sendBroadcast(new BroadcastMessage(BroadcastMessage.Type.USERDB_REQUEST));

        userDB = new ArrayList<>();

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
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        synchronized (userDB) {
            for (User user : userDB) {
                objectOutputStream.writeObject(user);
            }
        }

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
                        try {
                            sendUserDB(addr);
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
