package org.etudinsa.clavardage.sessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

class SessionListener extends Observable implements Runnable {

	final static int LISTENING_PORT = 1707;
	
	private final ServerSocket ssocket;

	SessionListener() throws IOException {
		this.ssocket = new ServerSocket(LISTENING_PORT);
		ssocket.setSoTimeout(5000);
	}
	
	public void run() {

		while(true) {

			if (ssocket.isClosed()) return;

			try {
				Socket client = ssocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String msg = in.readLine();

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
