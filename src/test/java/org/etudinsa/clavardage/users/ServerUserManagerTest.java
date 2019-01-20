package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;

public class ServerUserManagerTest {

    private static final String myPseudo = "Harley";
    private static KeyPair myKeyPair;
    private static InetAddress myIP;
    private static User myUser;
    private static ServerUserManager userManager;

    private static void setUp() throws Exception {
        userManager = new ServerUserManager(InetAddress.getByName("192.168.1.16"));
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);
        myKeyPair = keyGenerator.generateKeyPair();
        myIP = InetAddress.getByName("192.168.1.16");
        myUser = new User(myPseudo, myIP, myKeyPair.getPublic());
    }

    private static void testJoinNetworkWhenNotConnected() throws Exception {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            userManager.leaveNetwork();
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }
    }

    private static void testJoinNetworkWhenAlreadyConnected() throws Exception {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            userManager.joinNetwork(myPseudo, myKeyPair);
            throw new Exception("Should throw exception");
        } catch (Exception e) {
            assert(e.getMessage().equals("Already connected"));
        } finally {
            try {
                userManager.leaveNetwork();
            } catch (Exception e) {
                throw new Exception("Should not throw exception " + e.getMessage());
            }
        }
    }

    private static void testLeaveNetworkWhenConnected() throws Exception {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            userManager.leaveNetwork();
            assert(!userManager.isConnected());
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }
    }

    private static void testLeaveNetworkWhenNotConnected() {
        try {
            userManager.leaveNetwork();
            throw new Exception("Should throw exception");
        } catch (Exception e) {
            assert(e.getMessage().equals("Already disconnected"));
        }
    }

    private static void testGetUserByIpWithExistingUser() throws Exception {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            assert(myUser.equals(userManager.getUserByIp(myIP)));
            userManager.leaveNetwork();
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }
    }

    private static void testGetUserByIpWithNotExistingUser() throws Exception {
        try {
            assert(userManager.getUserByIp(myIP).equals(null));
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }
    }

    private static void testGetUserByPseudoWithExistingUser() throws Exception {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            assert(myUser.equals(userManager.getUserByPseudo(myPseudo)));
            userManager.leaveNetwork();
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }
    }

    private static void testGetUserByPseudoWithNotExistingUser() throws Exception {
        try {
            assert(userManager.getUserByPseudo(myPseudo).equals(null));
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }
    }

    private static void testgetUserDBWithOneUser() throws Exception {
        try {
            userManager.joinNetwork(myPseudo,myKeyPair);
            User[] expected = new User[1];
            expected[0] = myUser;
            User[] real;
            assert(userManager.isConnected());
            real = userManager.getUserDB();
            assert(real.length == 1);
            assert(real[0].equals(myUser));
            userManager.leaveNetwork();
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }

    }

    private static void testgetUserDBWithoutUser() throws Exception {
        try {
            assert(userManager.getUserDB().length == 0);
        } catch (Exception e) {
            throw new Exception("Should not throw exception " + e.getMessage());
        }

    }

    public static void main (String[] args) {
        try {
            setUp();
            testJoinNetworkWhenAlreadyConnected();
            testJoinNetworkWhenNotConnected();
            testGetUserByIpWithExistingUser();
            testGetUserByIpWithNotExistingUser();
            testGetUserByPseudoWithExistingUser();
            testGetUserByPseudoWithNotExistingUser();
            testgetUserDBWithOneUser();
            testgetUserDBWithoutUser();
            testLeaveNetworkWhenConnected();
            testLeaveNetworkWhenNotConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}