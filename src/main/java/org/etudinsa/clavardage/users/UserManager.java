package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.security.KeyPair;

public abstract class UserManager {

    private UserObserver userObserver;

    public void registerUserObserver(UserObserver uo) {
        this.userObserver = uo;
    }

    protected void notifyNewUser(User user) {
        if (userObserver != null)
            userObserver.newUser(user);
    }
    protected void notifyUserLeaving(User user) {
        if (userObserver != null)
            userObserver.userLeaving(user);
    }
    protected void notifyUpdatedUserList() {
        if (userObserver != null)
            userObserver.updatedUserList();
    }

    public abstract void joinNetwork(String pseudo, KeyPair keyPair) throws Exception;
    public abstract void leaveNetwork() throws Exception;
    public abstract void changePseudo(String pseudo) throws Exception;
    public abstract User getUserByIp(InetAddress ip);
    public abstract User getUserByPseudo(String pseudo);
    public abstract MyUser getMyUser();
    public abstract User[] getUserDB();
    public abstract boolean isConnected();
}
