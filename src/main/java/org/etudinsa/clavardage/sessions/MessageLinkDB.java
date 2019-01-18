package org.etudinsa.clavardage.sessions;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MessageLinkDB {

	static List<Message> messages;

	static Block<Document> createMsg = new Block<Document>() {
		@Override
		public void apply(final Document document) {
			Message msg;
			try {
				msg = new Message(document.getString("content"),
						InetAddress.getByName(document.getString("distantIP")),
						document.getBoolean("sent"),
						document.getDate("date"));
				messages.add(msg);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	};

	public static List<Message> getMessagesFromDB(InetAddress ip) {

		messages = new ArrayList<Message>();
		
		// Connect to MongoDB instance running on local host
		// Access database named 'clavardageDB'
		MongoClient mongoClient = new MongoClient();
		
		MongoDatabase database = mongoClient.getDatabase("clavardageDB");

		// Access collection named with the ip address of the distant use
		MongoCollection<Document> collection = database.getCollection(ip.toString());

		messages.clear();
		
		collection.find().forEach(createMsg);
		
		mongoClient.close();
		
		return messages;
	}
	
	public static void putMessagesInDB (List<Message> msgList, InetAddress ip) {
		// Connect to MongoDB instance running on local host
		// Access database named 'clavardageDB'
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("clavardageDB");

		// Access collection named with the ip address of the distant use
		MongoCollection<Document> collection = database.getCollection(ip.toString());
		
		collection.deleteMany(new Document());
		
		Document doc;
		for (Message msg : msgList) {
			doc = new Document()
					.append("sent", msg.isSent())
					.append("distantIP", msg.getDistantIP().getHostAddress())
					.append("content", msg.getContent().getContent())
					.append("date", msg.getContent().getDate());

			collection.insertOne(doc);
		}
		
		mongoClient.close();
	}

}
