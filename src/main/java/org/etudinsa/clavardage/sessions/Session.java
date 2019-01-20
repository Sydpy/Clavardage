package org.etudinsa.clavardage.sessions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.etudinsa.clavardage.users.User;

/**
 * Class to represent a chat between the current user and another user.
 */
public class Session {
	private User distantUser;
	private List<Message> messages;

	public Session(User distantUser) {
		this.distantUser = distantUser;
		this.messages = new ArrayList<Message>();
        //loadMessages();
	}

	/**
	 * Close the chat by saving all the messages exchanged.
	 */
	public void close() {
        //this.saveMessages();
	}

	public User getDistantUser() {
		return distantUser;
	}

	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * Add a new message to the chat.
	 * @param message to add
	 */
	public void addMessage(Message message) {
		this.messages.add(message);
	}

	/**
	 * Retrieve all the messages previously exchanged and stored in a database locally.
	 */
	private void loadMessages(){
		System.out.println("loading messages");
		List<Message> historic = MessageLinkDB.getMessagesFromDB(this.distantUser.ip);
		for (Message m: historic) {
			this.messages.add(m);
		}
	}

	/**
	 * Store in a database locally all the messages exchanged.
	 */
	private void saveMessages() {
		System.out.println("saving messages");
		MessageLinkDB.putMessagesInDB(this.messages, this.distantUser.ip);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Session s = (Session) obj;
		return Objects.equals(distantUser,s.getDistantUser());
	}

}
