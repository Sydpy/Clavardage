/**
 * 
 */
package org.etudinsa.clavardage.sessions;

import org.etudinsa.clavardage.users.LANUserManager;
import org.etudinsa.clavardage.users.MockUserManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 */
public class SessionManagerTest {
	
	private static final String myPseudo = "Joker";
	private static final String wrongPseudo = "Harley";
	private static LANUserManager userManager;
	private static SessionManagerImpl sessionManager;

	/**
	 * One user is created and added to the userDB
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyGenerator.initialize(1024, rng);

        userManager = new LANUserManager();
        sessionManager = new SessionManagerImpl(userManager);
        userManager.joinNetwork(myPseudo,keyGenerator.generateKeyPair());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			userManager.leaveNetwork();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Test
	public void testOpenSessionWithExistingUser() {
		try {
			assertNotNull(sessionManager.openSession(myPseudo));
			sessionManager.closeSession(myPseudo);
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}
	
	@Test
	public void testOpenSessionWithNotExistingUser() {
		try {
			sessionManager.openSession(wrongPseudo);
			fail("Should throw exception");
			sessionManager.closeSession(wrongPseudo);
		} catch (Exception e) {
			assert(e.getMessage().equals("No user with this pseudo: " + wrongPseudo));
		}
	}
	
	@Test
	public void testOpenSessionWithExistingSession() {
		try {
			assertNotNull(sessionManager.openSession(myPseudo));
			sessionManager.openSession(myPseudo);
			fail("Should throw exception");
		} catch (Exception e) {
			assert(e.getMessage().equals("Session already exists"));
		} finally {
			try {
				sessionManager.closeSession(myPseudo);
			} catch (Exception e) {
				fail("Should not throw exception " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testCloseSessionWithExistingUser() {
		try {
			assertNotNull(sessionManager.openSession(myPseudo));
			sessionManager.closeSession(myPseudo);			
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}
	
	@Test
	public void testCloseSessionWithNotExistingUser() {
		try {
			sessionManager.closeSession(wrongPseudo);
			fail("Should throw exception");
		} catch (Exception e) {
			assert(e.getMessage().equals("No user with this pseudo: " + wrongPseudo));
		}
	}
	
	@Test
	public void testCloseSessionWithNotExistingSession() {
		try {
			sessionManager.closeSession(myPseudo);
			fail("Should throw exception");
		} catch (Exception e) {
			assert(e.getMessage().equals("No session with a user with the pseudo: " + myPseudo));
		}
	}

	@Test
	public void testGetSessionByDistantUserPseudoWithNotExistingSession() {
		try {
			assertNotNull(sessionManager.getSessionByDistantUserPseudo(myPseudo));
			sessionManager.openSession(myPseudo);
			fail("Should throw exception");
		} catch (Exception e) {
			assert(e.getMessage().equals("Session already exists"));
		} finally {
			try {
				sessionManager.closeSession(myPseudo);
			} catch (Exception e) {
				fail("Should not throw exception " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetSessionByDistantUserPseudoWithNotExistingUser() {
		try {
			sessionManager.getSessionByDistantUserPseudo(wrongPseudo);
			fail("Should throw exception");
		} catch (Exception e) {
			assert(e.getMessage().equals("No user with this pseudo: " + wrongPseudo));
		}
	}
	
	@Test
	public void testGetSessionByDistantUserPseudoWithExistingSession() {
		try {
			Session expected = sessionManager.openSession(myPseudo);
			assertNotNull(expected);
			Session actual = sessionManager.getSessionByDistantUserPseudo(myPseudo);
			assertNotNull(actual);
			assertEquals(expected, actual);
			sessionManager.closeSession(myPseudo);
		} catch (Exception e) {
			fail("Should not throw exception " + e.getMessage());
		}
	}
	
	@Test
	public void testSendMessageToNotExistingUser() {
		try {
			sessionManager.sendMessage("hello", wrongPseudo);
			fail("Should throw exception");
		} catch (Exception e) {
			assert(e.getMessage().equals("No user with this pseudo: " + wrongPseudo));
		}
	}
	
}
