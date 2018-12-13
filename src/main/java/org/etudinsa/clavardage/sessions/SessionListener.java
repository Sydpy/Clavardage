package org.etudinsa.clavardage.sessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String content = in.readLine();
				Message msg = new Message(content,UserManager.getInstance().getMyUser(),UserManager.getInstance().getUserByIp(client.getInetAddress()));
				notifyObservers(msg);
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
