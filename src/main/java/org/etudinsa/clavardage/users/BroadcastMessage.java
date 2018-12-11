package org.etudinsa.clavardage.users;

import java.net.InetAddress;

class BroadcastMessage {

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
