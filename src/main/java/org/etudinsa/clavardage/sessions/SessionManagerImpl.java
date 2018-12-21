package org.etudinsa.clavardage.sessions;

import org.etudinsa.clavardage.users.MyUser;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

/**
 *
 * Class to manage all the active chats (open, close chats and send, receive messages in chats).
 *
 */
public class SessionManagerImpl extends SessionManager {

	private ObjectOutputStream objectOutputStream = null;

	private SessionListener sessionListener;

	public SessionManagerImpl(UserManager userManager) {
		super(userManager);
	}

	@Override
	public void startListening() {
		try {
			java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
			sessionListener = new SessionListener(this);

			Thread sessionListenerThread = new Thread(sessionListener);
			sessionListenerThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void stopListening() {
		sessionListener.stop();
		for (int i = 0; i < sessions.size(); i++) {
			sessions.get(i).close();
		}
	}

	/**
	 * Send a message with the given content to the user with the given pseudo.
	 * @param content of the message to send
	 * @param pseudo of the distant user
	 * @throws Exception thrown if the current user is not connected or
	 * 							if the user with the given pseudo is not active
	 */
	@Override
	public void sendMessage(String content, String pseudo) throws Exception {

		User receiver = userManager.getUserByPseudo(pseudo);
		MyUser myUser = userManager.getMyUser();

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

		Message message = new Message(messageContent, receiver.ip, true);

		session.addMessage(message);
		objectOutputStream.close();
		socket.close();

		//We notify the UI with the message sent
        notifyMessageSent(message);
	}

	/**
	 * Add the received message to the corresponding chat
	 * @param sigMsgContent content of the received message
	 * @param addr address of the sender
	 * @throws Exception
	 */
	void receivedMessageFrom(SignedMessageContent sigMsgContent, InetAddress addr)
			throws Exception {

		User sender = userManager.getUserByIp(addr);

		// Verify valid user
		if (sender == null) return;

		// Verify user signature
		if (!sender.verifySig(sigMsgContent.content, sigMsgContent.contentSignature))
			return;

		Session session = getSessionByDistantUserPseudo(sender.pseudo);

		User myUser = userManager.getMyUser();

		Message message = new Message(sigMsgContent.content, sender.ip, false);
		session.addMessage(message);

		notifyMessageReceived(message);
	}
}
