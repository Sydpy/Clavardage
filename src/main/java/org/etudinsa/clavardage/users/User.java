package org.etudinsa.clavardage.users;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.Objects;

public class User implements Serializable {

    public final String pseudo;
    public final InetAddress ip;

    User(String pseudo, InetAddress ip) {
        this.pseudo = pseudo;
        this.ip = ip;
    }
    
    @Override
    public String toString() {
    	return this.pseudo + " " + this.ip;
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
