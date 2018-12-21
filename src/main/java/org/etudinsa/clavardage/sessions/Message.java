package org.etudinsa.clavardage.sessions;

import java.net.InetAddress;
import java.util.Date;

/**
 * Class to represent the messages exchanged between users.
 *
 */
public class Message {

	private MessageContent content;
	private InetAddress distantIP;
	private boolean sent;

	public Message(MessageContent content, InetAddress distantIP, boolean sent) {
		this.content = content;
		this.distantIP = distantIP;
		this.sent = sent;
	}

	public Message(String content, InetAddress distantIP, boolean sent,  Date date) {
		this.content = new MessageContent(content, date);
		this.distantIP = distantIP;
		this.sent = sent;
	}

	public MessageContent getContent() {
		return content;
	}

	public InetAddress getDistantIP() {
		return distantIP;
	}

	public boolean isSent() {
		return sent;
	}

	public boolean isReceived() {
		return !sent;
	}
}
