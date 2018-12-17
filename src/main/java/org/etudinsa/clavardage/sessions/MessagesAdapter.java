package org.etudinsa.clavardage.sessions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

import org.etudinsa.clavardage.users.UserManager;

public class MessagesAdapter implements JsonbAdapter<Message, JsonObject> {

	@Override
	public Message adaptFromJson(JsonObject adapted) throws Exception {
		byte[] contentB = Base64.getMimeDecoder().decode(adapted.getString("content"));
		ByteArrayInputStream bis = new ByteArrayInputStream(contentB);
		ObjectInput in = new ObjectInputStream(bis);
		Serializable content = (Serializable) in.readObject();
		
		MessageContent msgContent = new MessageContent(content,new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(adapted.getString("date")));
		
		Message message = new Message(msgContent,
				UserManager.getInstance().getUserByPseudo(adapted.getString("recipient")),
				UserManager.getInstance().getUserByPseudo(adapted.getString("sender")));
		return message;
	}

	@Override
	public JsonObject adaptToJson(Message msg) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(msg.getContent().getContent());
		out.flush();
		byte[] contentB = bos.toByteArray();
		bos.close();
		String content = Base64.getMimeEncoder().encodeToString(contentB);
		return Json.createObjectBuilder()
				.add("content", content)
				.add("date", msg.getContent().getDate().toString())
				.add("sender", msg.getSender().pseudo)
				.add("recipient", msg.getRecipient().pseudo)
				.build();
	}

}
