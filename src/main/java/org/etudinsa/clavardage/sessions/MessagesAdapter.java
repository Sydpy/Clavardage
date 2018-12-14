package org.etudinsa.clavardage.sessions;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

import org.etudinsa.clavardage.users.UserManager;

public class MessagesAdapter implements JsonbAdapter<Message, JsonObject> {

	@Override
	public Message adaptFromJson(JsonObject adapted) throws Exception {
		Message message = new Message(adapted.getString("content")
				, UserManager.getInstance().getUserByPseudo(adapted.getString("recipient"))
				, UserManager.getInstance().getUserByPseudo(adapted.getString("sender"))
				, new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(adapted.getString("date")));
		return message;
	}

	@Override
	public JsonObject adaptToJson(Message msg) throws Exception {
		
		return Json.createObjectBuilder()
				.add("content", msg.getContent().getContent())
				.add("date", msg.getContent().getDate().toString())
				.add("sender", msg.getSender().pseudo)
				.add("recipient", msg.getRecipient().pseudo)
				.build();
	}

}
