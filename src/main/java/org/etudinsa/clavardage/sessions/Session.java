package org.etudinsa.clavardage.sessions;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.etudinsa.clavardage.users.User;

public class Session {
	private User distantUser;
	private List<Message> messages;
	private Socket socket;
	
	public Session(User distantUser) throws IOException {
		this.distantUser = distantUser;
		this.messages = new ArrayList<Message>();
		this.socket = new Socket(distantUser.getIp(),SessionListener.LISTENING_PORT);
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
	
	public Socket getSocket() {
		return socket;
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
	}
	
	private void loadMessages(){
		
	}
	
	private void saveMessages() {
		
	}
	
}
