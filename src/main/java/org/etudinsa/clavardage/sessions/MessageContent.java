package org.etudinsa.clavardage.sessions;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to represent the content of the messages exchanged.
 * The content must be a Serializable object.
 *
 */
public class MessageContent implements Serializable {
	
	private Serializable content;
	private Date date;
	
	public MessageContent(Serializable content) {
		this.content = content;
		this.date = new Date();
	}
	
	public MessageContent(Serializable content, Date date) {
		this.content = content;
		this.date = date;
	}

	public Serializable getContent() {
		return content;
	}

	public Date getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return this.content.toString() + " " + this.date.toString();
	}

}
