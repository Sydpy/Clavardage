package org.etudinsa.clavardage.sessions;

public class SessionManager {

    private static SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
    }

    private SessionManager() {
    }
}
