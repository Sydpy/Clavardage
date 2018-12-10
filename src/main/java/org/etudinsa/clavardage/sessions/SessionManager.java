package org.etudinsa.clavardage.sessions;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.etudinsa.clavardage.users.User;

public class SessionManager extends Observable implements Observer{
	
	private List<Session> sessions;
	private SessionListener sessionListener;

    private static SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
    }

    private SessionManager() {
    	this.sessions = new ArrayList<Session>();
    	this.sessionListener.addObserver(this);
    }
    
    public void openSession(User distantUser) {
    	this.sessions.add(new Session(distantUser));
    }
    
    public void closeSession(User distantUser) {
    	for (int i = 0; i < this.sessions.size(); i++) {
    		if (this.sessions.get(i).getDistantUser() == distantUser) {
    			this.sessions.get(i).close();
    			this.sessions.remove(i);
    		}
		}
    }
    
    public void sendMessage(String content) {
    	
    }
    
    private void receiveMessage(Message message) {
    	for (int i = 0; i < this.sessions.size(); i++) {
    		if (this.sessions.get(i).getDistantUser() == message.getSender()) {
    			this.sessions.get(i).addMessage(message);
    			this.sessions.remove(i);
    		}
		}
    }
   

	public void update(Observable o, Object arg) {
		SessionListener sl = (SessionListener) o;
		Message message = (Message) arg;
		receiveMessage(message);
	}
}
