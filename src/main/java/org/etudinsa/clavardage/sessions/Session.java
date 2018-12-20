package org.etudinsa.clavardage.sessions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.etudinsa.clavardage.users.User;

/**
 * Class to represent a chat between the current user and another user.
 */
public class Session {
	private User distantUser;
	private List<Message> messages;

	public Session(User distantUser) {
		this.distantUser = distantUser;
		this.messages = new ArrayList<Message>();
		loadMessages();
	}

	/**
	 * Close the chat by saving all the messages exchanged.
	 */
	public void close() {
		this.saveMessages();
	}

	public User getDistantUser() {
		return distantUser;
	}

	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * Add a new message to the chat.
	 * @param message to add
	 */
	public void addMessage(Message message) {
		this.messages.add(message);
	}

	/**
	 * Retrieve all the messages previously exchanged and stored as a Json file locally. 
	 */
	private void loadMessages(){
		String path = distantUser.pseudo + ".json";
		if ((new File(path)).exists()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(path));
				JsonbConfig config = new JsonbConfig().withAdapters(new MessageAdapter());
				Jsonb jsonb = JsonbBuilder.create(config);
				String line;
				Message msg;
				while ((line = br.readLine()) != null && (!line.equals(""))) {
					msg = jsonb.fromJson(line, Message.class);
					messages.add(msg);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Store locally all the messages exchanged as a Json file. 
	 */
	private void saveMessages() {
		JsonbConfig config = new JsonbConfig().withAdapters(new MessageAdapter());
		Jsonb jsonb = JsonbBuilder.create(config);
		StringBuilder sb = new StringBuilder();
		for (Message msg : messages) {
			sb.append(jsonb.toJson(msg));
			sb.append("\n");
		}
		File JSONOutputFile = new File(distantUser.pseudo + ".json");
		PrintWriter writer;
		try {
			writer = new PrintWriter(JSONOutputFile, "UTF-8");
			writer.println(sb.toString());
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
		Session s = (Session) obj;
		return Objects.equals(distantUser,s.getDistantUser());
	}

}
