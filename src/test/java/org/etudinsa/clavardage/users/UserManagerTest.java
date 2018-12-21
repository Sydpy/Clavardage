package org.etudinsa.clavardage.users;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class UserManagerTest {
	
	private static final String myPseudo = "Harley";
	private static KeyPair myKeyPair;
	private static LANUserManager userManager = LANUserManager.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);
        myKeyPair = keyGenerator.generateKeyPair();
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
			assertEquals(userManager.getMyUser(),userManager.getUserByIp(InetAddress.getLoopbackAddress()));
			userManager.leaveNetwork();
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}
	
	@Test
	public void testGetUserByIpWithNotExistingUser() {
		try {
			assertNull(userManager.getUserByIp(InetAddress.getLoopbackAddress()));
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}
	
	@Test
	public void testGetUserByPseudoWithExistingUser() {
		try {
			userManager.joinNetwork(myPseudo, myKeyPair);
			assert(userManager.isConnected());
			assertEquals(userManager.getMyUser(),userManager.getUserByPseudo(myPseudo));
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
	public void testIsUserDBAuthorityWhenNotConnected() {
		assert(!userManager.isUserDBAuthority());
	}
	
	@Test
	public void testIsUserDBAuthorityWhenNotInDB() {
		try {
			userManager.joinNetwork(myPseudo, myKeyPair);
			assert(userManager.isConnected());
			userManager.leaveNetwork();
			assert(!userManager.isUserDBAuthority());
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}
	
	@Test
	public void testIsUserDBAuthorityWhenLastUser() {
		try {
			userManager.joinNetwork(myPseudo, myKeyPair);
			assert(userManager.isConnected());
			assert(userManager.isUserDBAuthority());
			userManager.leaveNetwork();
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}

}
