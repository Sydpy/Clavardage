package org.etudinsa.clavardage.sessions;

public interface SessionObserver {
    void messageSent(Message message);

    void messageReceived(Message message);
}
