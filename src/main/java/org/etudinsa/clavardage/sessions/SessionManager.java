package org.etudinsa.clavardage.sessions;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;

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

	public void openSession(User distantUser) {
		try {
			this.sessions.add(new Session(distantUser));
		} catch (IOException e) {
			System.err.println("Session socket not created");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeSession(User distantUser) {
		try {
			Session session = getSessionByDistantUser(distantUser);
			session.close();
			this.sessions.remove(session);
		} catch (Exception e) {
			System.out.println("Impossible to close the session");
			e.printStackTrace();
		}
	}

	public void sendMessage(String content, User receiver) throws Exception {
		User myUser = UserManager.getInstance().getMyUser();
		if (myUser == null) {
			throw new Exception("You need to create a user !!");
		}
		Message message = new Message(content, receiver, myUser);
		Session session = getSessionByDistantUser(receiver);
		out = new PrintWriter(session.getSocket().getOutputStream(),true);
		out.println(message);
		session.addMessage(message);
	}

	private Session getSessionByDistantUser(User dUser) throws Exception {
		for (int i = 0; i < this.sessions.size(); i++) {
			if (this.sessions.get(i).getDistantUser() == dUser) {
				return this.sessions.get(i);
			}
		}
		throw new Exception("No Session with this user!!");
	}

	private void receiveMessage(Message message) {
		try {
			Session session = getSessionByDistantUser(message.getSender());
			session.addMessage(message);
			this.sessions.remove(session);
		} catch (Exception e) {
			System.out.println("Session needed to receive the message");
			e.printStackTrace();
		}
	}

	public void update(Observable o, Object arg) {
		SessionListener sl = (SessionListener) o;
		Message message = (Message) arg;
		receiveMessage(message);
	}
}
