package org.etudinsa.clavardage;

import org.etudinsa.clavardage.users.LANUserManager;
import org.etudinsa.clavardage.users.MockUserManager;
import org.etudinsa.clavardage.users.UserManager;

class UserManagerFactory {

    private static LANUserManager lanUserManager = null;
    private static MockUserManager mockUserManager = null;

    public static UserManager getLanUserManager() {
        if (lanUserManager == null)
            lanUserManager = new LANUserManager();

        return lanUserManager;
    }

    public static UserManager getMockUserManager() {

        if (mockUserManager == null)
            mockUserManager = new MockUserManager();

        return mockUserManager;
    }
}
