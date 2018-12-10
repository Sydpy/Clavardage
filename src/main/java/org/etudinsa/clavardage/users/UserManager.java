package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UserManager implements Observer {

    private static UserManager instance = new UserManager();

    public static UserManager getInstance() {
        return instance;
    }

    private List<User> userDB = new ArrayList<User>();
    private User myUser;

    private UserManager() {
        UserListener userListener = new UserListener();
        userListener.addObserver(this);

        Thread userListenerThread = new Thread(userListener);
        userListenerThread.start();
    }

    public User getUserByIp(InetAddress ip) {
        return null;
    }

    public User getUserByPseudo(String pseudo) {
        return null;
    }

    public User getMyUser() {
        return myUser;
    }

    public void changeMyPseudo(String myPseudo) {
        myUser.setPseudo(myPseudo);
    }

    public List<User> getUserDB() {
        return userDB;
    }

    private void retrieveUserDB() {

    }

    private void advertiseMyself() {

    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof List) {
            this.userDB = (List<User>) o;
        } else if (o instanceof User) {
            this.userDB.add((User) o);
        }
        else if (o instanceof InetAddress) {
            //TODO
        }

    }
}
