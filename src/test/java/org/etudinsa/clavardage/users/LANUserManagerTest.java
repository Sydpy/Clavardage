package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;

public class LANUserManagerTest {
	
	private static final String myPseudo = "Harley";
	private static KeyPair myKeyPair;
	private static LANUserManager userManager = new LANUserManager();

	private static void setUp() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);
        myKeyPair = keyGenerator.generateKeyPair();
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
			assert(userManager.getMyUser().equals(userManager.getUserByIp(InetAddress.getLoopbackAddress())));
			userManager.leaveNetwork();
		} catch (Exception e) {
			throw new Exception("Should not throw exception " + e.getMessage());
		}
	}

	private static void testGetUserByIpWithNotExistingUser() throws Exception {
		try {
			assert(userManager.getUserByIp(InetAddress.getLoopbackAddress()) == null);
		} catch (Exception e) {
			throw new Exception("Should not throw exception " + e.getMessage());
		}
	}

	private static void testGetUserByPseudoWithExistingUser() throws Exception {
		try {
			userManager.joinNetwork(myPseudo, myKeyPair);
			assert(userManager.isConnected());
			assert(userManager.getMyUser().equals(userManager.getUserByPseudo(myPseudo)));
			userManager.leaveNetwork();
		} catch (Exception e) {
			throw new Exception("Should not throw exception " + e.getMessage());
		}
	}

	private static void testGetUserByPseudoWithNotExistingUser() throws Exception {
		try {
			assert(userManager.getUserByPseudo(myPseudo) == null);
		} catch (Exception e) {
			throw new Exception("Should not throw exception " + e.getMessage());
		}
	}

	private static void testIsUserDBAuthorityWhenNotConnected() {
		assert(!userManager.isUserDBAuthority());
	}

	private static void testIsUserDBAuthorityWhenNotInDB() throws Exception {
		try {
			userManager.joinNetwork(myPseudo, myKeyPair);
			assert(userManager.isConnected());
			userManager.leaveNetwork();
			assert(!userManager.isUserDBAuthority());
		} catch (Exception e) {
			throw new Exception("Should not throw exception " + e.getMessage());
		}
	}

	private static void testIsUserDBAuthorityWhenLastUser() throws Exception {
		try {
			userManager.joinNetwork(myPseudo, myKeyPair);
			assert(userManager.isConnected());
			assert(userManager.isUserDBAuthority());
			userManager.leaveNetwork();
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
			testLeaveNetworkWhenConnected();
			testLeaveNetworkWhenNotConnected();
			testIsUserDBAuthorityWhenLastUser();
			testIsUserDBAuthorityWhenNotConnected();
			testIsUserDBAuthorityWhenNotInDB();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	


}
