package org.etudinsa.clavardage.sessions;

import java.util.ArrayList;
import java.util.List;

import org.etudinsa.clavardage.users.User;

public class Session {
	private User distantUser;
	private List<Message> messages;
	
	public Session(User distantUser) {
		this.distantUser = distantUser;
		this.messages = new ArrayList<Message>();
	}
	
	public void close() {
		this.saveMessages();
	}

	public User getDistantUser() {
		return distantUser;
	}

	public List<Message> getMessages() {
		return messages;
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
	}
	
	private void loadMessages(){
		
	}
	
	private void saveMessages() {
		
	}
	
}
