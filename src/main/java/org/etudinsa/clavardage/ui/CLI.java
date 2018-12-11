package org.etudinsa.clavardage.ui;

import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.UserManager;

import java.util.Observable;

public class CLI extends UI implements Runnable {

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

    @Override
    public void run() {

        System.out.println("Starting ClavardageCLI.");

        System.out.println("Doing something.");

        System.out.println("Exiting ClavardageCLI.");
    }

    public static void main(String[] args) {

        UserManager.getInstance().start();
        SessionManager.getInstance().start();

        new CLI().run();

        SessionManager.getInstance().stop();
        UserManager.getInstance().stop();
    }

}
