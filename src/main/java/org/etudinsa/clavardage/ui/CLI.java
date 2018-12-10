package org.etudinsa.clavardage.ui;

import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.UserManager;

import java.util.Observable;

public class CLI extends UI {

    private static CLI instance = new CLI();

    public static CLI getInstance() {
        return instance;
    }

    private CLI() {
        UserManager.getInstance().addObserver(this);
        SessionManager.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof UserManager) {
            UserManager um = (UserManager) observable;
            //TODO

        } else if (observable instanceof SessionManager) {
            SessionManager sm = (SessionManager) observable;
            //TODO

        }

    }
}
