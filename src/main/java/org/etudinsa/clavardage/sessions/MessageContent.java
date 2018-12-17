package org.etudinsa.clavardage.sessions;

import java.io.Serializable;
import java.util.Date;

public class MessageContent implements Serializable {
	
	private String content;
	private Date date;
	
	public MessageContent(String content) {
		this.content = content;
		this.date = new Date();
	}
	
	public MessageContent(String content, Date date) {
		this.content = content;
		this.date = date;
	}

	public String getContent() {
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
