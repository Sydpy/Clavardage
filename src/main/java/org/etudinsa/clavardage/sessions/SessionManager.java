package org.etudinsa.clavardage.sessions;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.etudinsa.clavardage.users.UserManager;
import org.etudinsa.clavardage.users.User;

public class SessionManager extends Observable implements Observer{
	
    private static SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
    }
    
	private List<Session> sessions = new ArrayList<>();
	private PrintWriter out = null;

	private SessionListener sessionListener;

	private SessionManager() {}

	public void start() {
		try {
			sessionListener = new SessionListener();

			sessionListener.addObserver(this);

			Thread sessionListenerThread = new Thread(sessionListener);
			sessionListenerThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public void stop() {
	   sessionListener.stop();
	}

	public Session openSession(String pseudo) throws IOException {
		User distantUser = UserManager.getInstance().getUserByPseudo(pseudo);
		Session nSession = new Session(distantUser);
		this.sessions.add(nSession);
		return nSession;
	}

	public void closeSession(String pseudo) {
		try {
			Session session = getSessionByDistantUserPseudo(pseudo);
			session.close();
			this.sessions.remove(session);
		} catch (Exception e) {
			System.out.println("Impossible to close the session");
			e.printStackTrace();
		}
	}

	//We get the session between 2 users or create it if it doesn't exist 
	//and we create a socket to send the message
	//We notify the UI with the message sent
	public void sendMessage(String content, String pseudo) throws Exception {
		User receiver = UserManager.getInstance().getUserByPseudo(pseudo);
		User myUser = UserManager.getInstance().getMyUser();
		if (myUser == null) {
			throw new Exception("You need to create a user!!");
		}
		if (receiver == null) {
			throw new Exception("No user with this pseudo!!");
		}
		Message message = new Message(content, receiver, myUser);
		Session session = getSessionByDistantUserPseudo(pseudo);
		if (session == null) {
			session = openSession(pseudo);
		}
		Socket socket = new Socket(receiver.ip,SessionListener.LISTENING_PORT);
		out = new PrintWriter(socket.getOutputStream(),true);
		out.println(message.getContent());
		session.addMessage(message);
		socket.close();
		
		setChanged();
		notifyObservers(session);
	}

	public Session getSessionByDistantUserPseudo(String pseudo) throws Exception {
		User dUser = UserManager.getInstance().getUserByPseudo(pseudo);
		if (dUser == null) {
			throw new Exception("No user with this pseudo!!");
		}
		for (int i = 0; i < this.sessions.size(); i++) {
			if (this.sessions.get(i).getDistantUser() == dUser) {
				return this.sessions.get(i);
			}
		}
		return null;
	}

	//When we receive a message, we add it to the session or create one if it doesn't already exist
	private void receiveMessage(Message message) {
		try {
			Session session = getSessionByDistantUserPseudo(message.getSender().pseudo);
			if (session == null) {
				session = openSession(message.getSender().pseudo);
			}
			session.addMessage(message);
			
			setChanged();
			notifyObservers(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(Observable o, Object arg) {
		SessionListener sl = (SessionListener) o;
		Message message = (Message) arg;
		receiveMessage(message);
	}
}
