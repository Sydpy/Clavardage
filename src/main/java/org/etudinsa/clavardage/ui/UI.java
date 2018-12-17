package org.etudinsa.clavardage.ui;

import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.users.User;

public interface UI {
    void messageSent(Message message);
    void messageReceived(Message message);

    void newUser(User newUser);
    void userLeaving(User userLeaving);
}
