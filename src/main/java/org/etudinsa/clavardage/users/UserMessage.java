package org.etudinsa.clavardage.users;

import java.io.Serializable;

public class UserMessage implements Serializable {

    public enum Type { USERDB_REQUEST, NEWUSER, USERLEAVING }

    public final Type type;
    public final String[] content;

    UserMessage(Type type, String... content) {
        this.type = type;
        this.content = content;
    }

    static UserMessage fromString(String str) throws Exception {

    	String[] splitted = str.split(":", 2);
    	
    	Type type = Type.valueOf(splitted[0].trim());

    	if (splitted.length == 1) {
    	    return new UserMessage(type);
        }

    	return new UserMessage(type, splitted[1].split(":"));
    }
    
    @Override
    public String toString() {
		StringBuilder sb = new StringBuilder(type.toString());

        for (String s : content) {
            sb.append(":");
            sb.append(s.trim());
        }

		return sb.toString();
    }
}
