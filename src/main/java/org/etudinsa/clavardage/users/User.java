package org.etudinsa.clavardage.users;

import java.net.InetAddress;

public class User {
    private String pseudo;
    private InetAddress ip;

    public User(String pseudo, InetAddress ip) {
        this.pseudo = pseudo;
        this.ip = ip;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public InetAddress getIp() {
        return ip;
    }
}
