package org.etudinsa.clavardage.sessions;

import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

public class MockSessionManager extends SessionManager {

    public MockSessionManager(UserManager userManager) {
        super(userManager);
    }

    @Override
    public void startListening() {
    }

    @Override
    public void stopListening() {
    }

    @Override
    public void sendMessage(String content, String pseudo) {

        User distantUser = userManager.getUserByPseudo(pseudo);

        MessageContent sentContent = new MessageContent(content);
        Message sent = new Message(sentContent, distantUser.ip, true);

        notifyMessageSent(sent);

        MessageContent receivedContent = new MessageContent("J'ai bien reçu, je vais voir ce que je peux faire.");
        Message received = new Message(receivedContent, distantUser.ip, false);

        notifyMessageReceived(received);
    }
}
