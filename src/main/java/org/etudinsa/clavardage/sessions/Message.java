package org.etudinsa.clavardage.sessions;

import java.util.Date;

import org.etudinsa.clavardage.users.User;

public class Message {
	private MessageContent content;
	private User sender;
	private User recipient;
	
	public Message(MessageContent content, User destUser, User srcUser) {
		this.content = content;
		this.recipient = destUser;
		this.sender = srcUser;
	}
	
	public Message(String content, User destUser, User srcUser, Date date) {
		this.content = new MessageContent(content, date);
		this.recipient = destUser;
		this.sender = srcUser;
	}

	public MessageContent getContent() {
		return content;
	}

	public User getSender() {
		return sender;
	}

	public User getRecipient() {
		return recipient;
	}

	@Override
	public String toString() {
		return "sender: " + sender.toString() + " recipient: " + recipient.toString() + " msg: " + content.toString();
	}
	
}
