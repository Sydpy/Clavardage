package org.etudinsa.clavardage.sessions;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * Class that listens on a port to receive all the messages concerning chats, using a ServerSocket. 
 *
 */
class SessionListener implements Runnable {

	final static int LISTENING_PORT = 1707;
	
	private final ServerSocket ssocket;
	private final SessionManagerImpl sessionManager;

	SessionListener(SessionManagerImpl sm) throws IOException {
		this.ssocket = new ServerSocket(LISTENING_PORT);
		this.sessionManager = sm;
	}

	/**
	 * Listens on the port LISTENING_PORT and calls the SessionManagerImpl to process the messages received.
	 */
	public void run() {

		while(!ssocket.isClosed()) {

			try {
				Socket client = ssocket.accept();
				InputStream is = client.getInputStream();
				ObjectInputStream objectInputStream = new ObjectInputStream(is);
				SignedMessageContent sigMsgContent = (SignedMessageContent) objectInputStream.readObject();

				sessionManager.receivedMessageFrom(sigMsgContent, client.getInetAddress());

				client.close();

			} catch (EOFException ignored) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void stop() {
		try {
			ssocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
