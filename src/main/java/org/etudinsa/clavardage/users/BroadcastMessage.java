package org.etudinsa.clavardage.users;

import java.io.Serializable;
import java.net.InetAddress;

class BroadcastMessage implements Serializable {

    enum Type { USERDB_REQUEST, NEWUSER, USERLEAVING }

    final Type type;
    User user;

    BroadcastMessage(Type type) {
        this.type = type;
    }

    BroadcastMessage(Type type, User user) {
        this.type = type;
        this.user = user;
    }
}
