package org.etudinsa.clavardage.sessions;

import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SessionManager {

    private SessionObserver sessionObserver;

    protected UserManager userManager;
    protected List<Session> sessions = new ArrayList<>();

    public SessionManager(UserManager userManager) {
        this.userManager = userManager;
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
    }

    public void registerSessionObserver(SessionObserver so) {
        this.sessionObserver = so;
    }

    protected void notifyMessageReceived(Message message) {
        this.sessionObserver.messageReceived(message);
    }
    protected void notifyMessageSent(Message message) {
        this.sessionObserver.messageSent(message);
    }


    public abstract void startListening();
    public abstract void stopListening();

    /**
     * Create a new chat with the user with the given pseudo.
     * @param pseudo of the distant user
     * @return the chat created
     * @throws Exception thrown if the user with the given pseudo is not active or
     * 							if a chat with this user is already open
     */
    public Session openSession(String pseudo) throws Exception {
        User distantUser = userManager.getUserByPseudo(pseudo);
        if (distantUser == null) {
            throw new Exception("No user with this pseudo: " + pseudo);
        }
        Session nSession = new Session(distantUser);
        if (this.sessions.contains(nSession)) {
            throw new Exception("Session already exists");
        }
        this.sessions.add(nSession);
        return nSession;
    }

    /**
     * Close the active chat with the user with the given pseudo.
     * @param pseudo of the distant user
     * @throws Exception thrown if the user with the given pseudo is not active or
     * 							if there is no active chat with the user
     */
    public void closeSession(String pseudo) throws Exception {
        Session session;
        User dUser = userManager.getUserByPseudo(pseudo);
        if (dUser == null) {
            throw new Exception("No user with this pseudo: " + pseudo);
        }
        boolean removed =  false;
        for (int i = 0; i < this.sessions.size(); i++) {
            if (this.sessions.get(i).getDistantUser() == dUser) {
                session = sessions.get(i);
                session.close();
                this.sessions.remove(session);
                removed = true;
            }
        }
        if (!removed) {
            throw new Exception("No session with a user with the pseudo: " + pseudo);
        }
    }

    /**
     * Retrieve the chat with the user with the given pseudo or create it if it doesn't exist.
     * @param pseudo of the distant user
     * @return the chat
     * @throws Exception thrown if the user with the given pseudo is not active
     */
    public Session getSessionByDistantUserPseudo(String pseudo) throws Exception {
        User dUser = userManager.getUserByPseudo(pseudo);
        if (dUser == null) {
            throw new Exception("No user with this pseudo: " + pseudo);
        }
        for (int i = 0; i < this.sessions.size(); i++) {
            if (this.sessions.get(i).getDistantUser() == dUser) {
                return this.sessions.get(i);
            }
        }
        return openSession(pseudo);
    }

    public abstract void sendMessage(String content, String pseudo) throws Exception;

}
