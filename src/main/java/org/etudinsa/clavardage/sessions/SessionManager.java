package org.etudinsa.clavardage.sessions;

import org.etudinsa.clavardage.ui.UI;
import org.etudinsa.clavardage.users.MyUser;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
	
    private static SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
    }
    
	private List<Session> sessions = new ArrayList<>();
	private ObjectOutputStream objectOutputStream = null;

	private SessionListener sessionListener;

	private UI ui;

	private SessionManager() {}

	public void registerUI(UI ui) {
		this.ui = ui;
	}

	public void start() {
		try {
			sessionListener = new SessionListener();

			Thread sessionListenerThread = new Thread(sessionListener);
			sessionListenerThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void stop() {
	   sessionListener.stop();
	   for (int i = 0; i < sessions.size(); i++) {
		   sessions.get(i).close();
		   }
	}

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

	//We get the session between 2 users or create it if it doesn't exist 
	//and we create a socket to send the message
	//We notify the UI with the message sent
	public void sendMessage(String content, String pseudo) throws Exception {
		User receiver = UserManager.getInstance().getUserByPseudo(pseudo);
		MyUser myUser = UserManager.getInstance().getMyUser();
		if (myUser == null) {
			throw new Exception("You need to create a user!!");
		}
		if (receiver == null) {
			throw new Exception("No user with this pseudo: " + pseudo);
		}
		Session session = getSessionByDistantUserPseudo(pseudo);
		MessageContent messageContent = new MessageContent(content);
		String signature = myUser.signObject(messageContent);
		SignedMessageContent sigMsgContent = new SignedMessageContent(messageContent, signature);

		Socket socket = new Socket(receiver.ip,SessionListener.LISTENING_PORT);
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(sigMsgContent);
        objectOutputStream.flush();

		Message message = new Message(messageContent, receiver, myUser);

		session.addMessage(message);
		objectOutputStream.close();
		socket.close();

		ui.messageSent(message);
	}

	//Get a session by the pseudo of the distant user or create one if it doesn't exist
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

		ui.messageReceived(message);
	}
}
