package org.etudinsa.clavardage;

import org.etudinsa.clavardage.sessions.MockSessionManager;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.sessions.SessionManagerImpl;

class SessionManagerFactory {

    private static SessionManagerImpl sessionManager = null;

    private static MockSessionManager mockSessionManager = null;

    public static SessionManager getSessionManager() {
        if (sessionManager == null)
            sessionManager = new SessionManagerImpl(UserManagerFactory.getLanUserManager());

        return sessionManager;
    }

    public static SessionManager getMockSessionManager() {
        if (mockSessionManager == null)
            mockSessionManager = new MockSessionManager(UserManagerFactory.getMockUserManager());
        return mockSessionManager;
    }
}
