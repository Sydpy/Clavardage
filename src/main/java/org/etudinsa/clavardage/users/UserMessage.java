package org.etudinsa.clavardage.users;

import java.io.Serializable;

class UserMessage implements Serializable {

    enum Type { USERDB_REQUEST, NEWUSER, USERLEAVING }

    final Type type;
    String pseudo;

    UserMessage(Type type) {
        this.type = type;
    }

    UserMessage(Type type, String pseudo) {
        this.type = type;
        this.pseudo = pseudo;
    }
    
    public static UserMessage fromString(String str) throws Exception {
    	
    	String[] splitted = str.split(":", 2);
    	
    	Type type = Type.valueOf(splitted[0].trim());
    	
    	if (type == Type.NEWUSER) {
    		if (splitted.length != 2)
    			throw new Exception("Invalid string");
    		
    		String pseudo = splitted[1].trim();
    		
    		return new UserMessage(type, pseudo);
    	}
    	
    	return new UserMessage(type);
    }
    
    @Override
    public String toString() {
		StringBuilder sb = new StringBuilder(type.toString());
		
		if (pseudo != null) {
			sb.append(":");
			sb.append(pseudo);
		}
		
		return sb.toString();
    }
}
