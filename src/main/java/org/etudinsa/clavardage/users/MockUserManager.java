package org.etudinsa.clavardage.users;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockUserManager extends UserManager {

    private boolean connected = false;
    private MyUser myUser;
    private List<User> userDB = new ArrayList<User>();

    public MockUserManager() {


        for (int i = 0; i < 10; i++) {
            try {
                String pseudo = String.format("Mock%d", i);
                newUser(pseudo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void newUser(String pseudo) throws Exception {

        Random random = new Random();

        String ip = String.format("%d.%d.%d.%d", random.nextInt(255), random.nextInt(255), random.nextInt(255), random.nextInt(255));

        InetAddress addr = InetAddress.getByName(ip);

        BigInteger mod = BigInteger.probablePrime(512, random);
        BigInteger exp = BigInteger.probablePrime(256, random);
        PublicKey pKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(mod, exp));

        User user = new User(pseudo, addr, pKey);
        userDB.add(user);

        if (connected) notifyNewUser(user);
    }

    public void userLeaving(String pseudo) {

        User user = getUserByPseudo(pseudo);

        if (user == null) return;

        userDB.remove(user);
        if (connected) notifyUserLeaving(user);
    }

    @Override
    public void joinNetwork(String pseudo, KeyPair keyPair) throws Exception {
        myUser = new MyUser(pseudo, InetAddress.getLoopbackAddress(), keyPair);
        userDB.add(myUser);
        this.connected = true;
    }

    @Override
    public void leaveNetwork() throws Exception {
        this.connected = false;
    }

    @Override
    public void changePseudo(String pseudo) throws Exception {
    }

    @Override
    public User getUserByIp(InetAddress ip) {

        for (User user : userDB) {
            if (user.ip.equals(ip))
                return user;
        }
        return null;
    }

    @Override
    public User getUserByPseudo(String pseudo) {
        for (User user : userDB) {
            if (user.pseudo.equals(pseudo))
                return user;
        }
        return null;
    }

    @Override
    public MyUser getMyUser() {
        return myUser;
    }

    @Override
    public User[] getUserDB() {
        return userDB.toArray(new User[userDB.size()]);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
