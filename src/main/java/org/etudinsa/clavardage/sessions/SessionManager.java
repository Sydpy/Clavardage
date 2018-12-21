package org.etudinsa.clavardage.sessions;

import org.etudinsa.clavardage.users.MyUser;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * Class to manage all the active chats (open, close chats and send, receive messages in chats).
 *
 */
public class SessionManager {

	private static SessionManager instance = new SessionManager();

	public static SessionManager getInstance() {
		return instance;
	}

	/**
	 * All the active chats.
	 */
	private List<Session> sessions = new ArrayList<>();
	private ObjectOutputStream objectOutputStream = null;

	private SessionListener sessionListener;

	private SessionObserver sessionObserver;

	private SessionManager() {}

	public void registerSessionObserver(SessionObserver so) {
		this.sessionObserver = so;
	}

	/**
	 * Start a thread executing the SessionListener.
	 */
	public void start() {
		try {
			java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
			sessionListener = new SessionListener();

			Thread sessionListenerThread = new Thread(sessionListener);
			sessionListenerThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Stop the thread executing the SessionListener and all the open chats.
	 */
	public void stop() {
		sessionListener.stop();
		for (int i = 0; i < sessions.size(); i++) {
			sessions.get(i).close();
		}
	}

	/**
	 * Create a new chat with the user with the given pseudo.
	 * @param pseudo of the distant user
	 * @return the chat created
	 * @throws Exception thrown if the user with the given pseudo is not active or
	 * 							if a chat with this user is already open
	 */
	public Session openSession(String pseudo) throws Exception {
		User distantUser = UserManager.getInstance().getUserByPseudo(pseudo);
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
		User dUser = UserManager.getInstance().getUserByPseudo(pseudo);
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
	 * Send a message with the given content to the user with the given pseudo.
	 * @param content of the message to send
	 * @param pseudo of the distant user
	 * @throws Exception thrown if the current user is not connected or
	 * 							if the user with the given pseudo is not active
	 */
	public void sendMessage(String content, String pseudo) throws Exception {
		User receiver = UserManager.getInstance().getUserByPseudo(pseudo);
		MyUser myUser = UserManager.getInstance().getMyUser();
		if (myUser == null) {
			throw new Exception("You need to create a user!!");
		}
		if (receiver == null) {
			throw new Exception("No user with this pseudo: " + pseudo);
		}
		//We get the session between 2 users or create it if it doesn't exist 
		Session session = getSessionByDistantUserPseudo(pseudo);
		MessageContent messageContent = new MessageContent(content);
		String signature = myUser.signObject(messageContent);
		SignedMessageContent sigMsgContent = new SignedMessageContent(messageContent, signature);

		//We create a socket to send the message
		Socket socket = new Socket(receiver.ip,SessionListener.LISTENING_PORT);
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(sigMsgContent);
		objectOutputStream.flush();

		Message message = new Message(messageContent, receiver, myUser);

		session.addMessage(message);
		objectOutputStream.close();
		socket.close();

		//We notify the UI with the message sent
		if (sessionObserver != null) sessionObserver.messageSent(message);
	}

	/**
	 * Retrieve the chat with the user with the given pseudo or create it if it doesn't exist.
	 * @param pseudo of the distant user
	 * @return the chat
	 * @throws Exception thrown if the user with the given pseudo is not active
	 */
	public Session getSessionByDistantUserPseudo(String pseudo) throws Exception {
		User dUser = UserManager.getInstance().getUserByPseudo(pseudo);
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

	/**
	 * Add the received message to the corresponding chat
	 * @param sigMsgContent content of the received message
	 * @param addr address of the sender
	 * @throws Exception
	 */
	public void receivedMessageFrom(SignedMessageContent sigMsgContent, InetAddress addr)
			throws Exception {

		User sender = UserManager.getInstance().getUserByIp(addr);

		// Verify valid user
		if (sender == null) return;

		// Verify user signature
		if (!sender.verifySig(sigMsgContent.content, sigMsgContent.contentSignature))
			return;

		Session session = getSessionByDistantUserPseudo(sender.pseudo);

		User myUser = UserManager.getInstance().getMyUser();

		Message message = new Message(sigMsgContent.content, myUser, sender);
		session.addMessage(message);

		if (sessionObserver != null) sessionObserver.messageReceived(message);
	}
}
