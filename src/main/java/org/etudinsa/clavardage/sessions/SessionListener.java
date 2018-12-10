package org.etudinsa.clavardage.sessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

class SessionListener extends Observable implements Runnable {

	final static int LISTENING_PORT = 1707;
	
	private ServerSocket ssocket;
	
	public SessionListener() throws IOException {
		this.ssocket = new ServerSocket(LISTENING_PORT);
	}	
	
	public void run() {
		boolean not_done = true;
		while(not_done) {
			try {
				Socket client = ssocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String msg = in.readLine();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
