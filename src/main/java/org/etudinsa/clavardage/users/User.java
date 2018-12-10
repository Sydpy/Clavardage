package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(pseudo, user.pseudo) &&
                Objects.equals(ip, user.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo, ip);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
