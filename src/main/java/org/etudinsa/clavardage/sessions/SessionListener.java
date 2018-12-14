package org.etudinsa.clavardage.sessions;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

import org.etudinsa.clavardage.users.UserManager;

class SessionListener extends Observable implements Runnable {

	final static int LISTENING_PORT = 1707;
	
	private final ServerSocket ssocket;

	SessionListener() throws IOException {
		this.ssocket = new ServerSocket(LISTENING_PORT);
	}
	
	public void run() {

		while(!ssocket.isClosed()) {

			try {
				Socket client = ssocket.accept();
	            InputStream is = client.getInputStream();
	            ObjectInputStream objectInputStream = new ObjectInputStream(is);
	            try {
					MessageContent msgContent = (MessageContent) objectInputStream.readObject();
					Message msg = new Message(msgContent.getContent(),UserManager.getInstance().getMyUser(),UserManager.getInstance().getUserByIp(client.getInetAddress()),msgContent.getDate());
					setChanged();
					notifyObservers(msg);
				} catch (EOFException ef) {
                    System.out.println("EOFException in SessionListener");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
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
