package org.etudinsa.clavardage;

import org.etudinsa.clavardage.sessions.MockSessionManager;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.sessions.SessionManagerImpl;
import org.etudinsa.clavardage.users.LANUserManager;
import org.etudinsa.clavardage.users.ServerUserManager;
import org.etudinsa.clavardage.users.MockUserManager;
import org.etudinsa.clavardage.users.UserManager;

import java.lang.reflect.Constructor;
import java.net.InetAddress;

class ManagerFactory {

    private static enum Mode { LAN, MOCK, SERVER }

    private Mode mode;

    private SessionManager sessionManager;
    private UserManager userManager;

    private InetAddress serverAddress;

    ManagerFactory(boolean mock) {
        if (mock) {
            mode = Mode.MOCK;
        } else {
            mode = Mode.LAN;
        }
    }

    ManagerFactory(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
        mode = Mode.SERVER;
    }

    public SessionManager getSessionManager() {

        if (sessionManager == null) {
            switch (mode) {
                case LAN:
                    sessionManager = new SessionManagerImpl(getUserManager());
                    break;
                case MOCK:
                    sessionManager = new MockSessionManager(getUserManager());
                    break;
                case SERVER:
                    sessionManager = new SessionManagerImpl(getUserManager());
                    break;
            }
        }

        return sessionManager;
    }

    public UserManager getUserManager() {

        if (userManager == null) {
            switch (mode) {
                case LAN:
                    userManager = new LANUserManager();
                    break;
                case MOCK:
                    userManager = new MockUserManager();
                    break;
                case SERVER:
                    userManager = new ServerUserManager(serverAddress);
                    break;
            }
        }

        return userManager;
    }
}
