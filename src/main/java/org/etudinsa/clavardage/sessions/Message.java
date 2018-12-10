package org.etudinsa.clavardage.sessions;

import java.util.Date;

import org.etudinsa.clavardage.users.User;

public class Message {
	private String content;
	private Date date;
	private User sender;
	private User recipient;
	
	public Message(String content) {
		this.content = content;
		this.date = new Date();
	}

	public String getContent() {
		return content;
	}

	public Date getDate() {
		return date;
	}

	public User getSender() {
		return sender;
	}

	public User getRecipient() {
		return recipient;
	}

	
}
