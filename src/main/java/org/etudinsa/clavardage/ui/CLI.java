package org.etudinsa.clavardage.ui;

public class CLI implements UI {

    private static CLI instance = new CLI();

    public static CLI getInstance() {
        return instance;
    }

    private CLI() {
    }
}
