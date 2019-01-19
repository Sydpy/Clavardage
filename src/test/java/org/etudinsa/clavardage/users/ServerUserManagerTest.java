package org.etudinsa.clavardage.users;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;

import static org.junit.Assert.*;

public class ServerUserManagerTest {

    private static final String myPseudo = "Harley";
    private static KeyPair myKeyPair;
    private static InetAddress myIP;
    private static User myUser;
    private static ServerUserManager userManager;

    @Before
    public void setUp() throws Exception {
        userManager = new ServerUserManager(InetAddress.getByName("192.168.1.16"));
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);
        myKeyPair = keyGenerator.generateKeyPair();
        myIP = InetAddress.getByName("192.168.1.16");
        myUser = new User(myPseudo, myIP, myKeyPair.getPublic());
    }

    @Test
    public void testJoinNetworkWhenNotConnected() {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            userManager.leaveNetwork();
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }
    }

    @Test
    public void testJoinNetworkWhenAlreadyConnected() {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            userManager.joinNetwork(myPseudo, myKeyPair);
            fail("Should throw exception");
        } catch (Exception e) {
            assert(e.getMessage().equals("Already connected"));
        } finally {
            try {
                userManager.leaveNetwork();
            } catch (Exception e) {
                fail("Should not throw exception " + e.getMessage());
            }
        }
    }

    @Test
    public void testLeaveNetworkWhenConnected() {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            userManager.leaveNetwork();
            assert(!userManager.isConnected());
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }
    }

    @Test
    public void testLeaveNetworkWhenNotConnected() {
        try {
            userManager.leaveNetwork();
            fail("Should throw exception");
        } catch (Exception e) {
            assert(e.getMessage().equals("Already disconnected"));
        }
    }

    @Test
    public void testGetUserByIpWithExistingUser() {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            assertEquals(myUser,userManager.getUserByIp(myIP));
            userManager.leaveNetwork();
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }
    }

    @Test
    public void testGetUserByIpWithNotExistingUser() {
        try {
            assertNull(userManager.getUserByIp(myIP));
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }
    }

    @Test
    public void testGetUserByPseudoWithExistingUser() {
        try {
            userManager.joinNetwork(myPseudo, myKeyPair);
            assert(userManager.isConnected());
            assertEquals(myUser,userManager.getUserByPseudo(myPseudo));
            userManager.leaveNetwork();
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }
    }

    @Test
    public void testGetUserByPseudoWithNotExistingUser() {
        try {
            assertNull(userManager.getUserByPseudo(myPseudo));
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }
    }

    @Test
    public void testgetUserDBWithOneUser() {
        try {
            userManager.joinNetwork(myPseudo,myKeyPair);
            User[] expected = new User[1];
            expected[0] = myUser;
            assert(userManager.isConnected());
            assertArrayEquals(expected,userManager.getUserDB());
            userManager.leaveNetwork();
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }

    }

    @Test
    public void testgetUserDBWithoutUser() {
        try {
            assertEquals(0, userManager.getUserDB().length);
        } catch (Exception e) {
            fail("Should not throw exception " + e.getMessage());
        }

    }

}