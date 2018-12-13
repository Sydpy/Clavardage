package org.etudinsa.clavardage.users;

import java.io.Serializable;
import java.net.InetAddress;

class BroadcastMessage implements Serializable {

    static enum Type { USERDB_REQUEST, NEWUSER, USERLEAVING }

    final Type type;
    String pseudo;

    BroadcastMessage(Type type) {
        this.type = type;
    }

    BroadcastMessage(Type type, String pseudo) {
        this.type = type;
        this.pseudo = pseudo;
    }
    
    public static BroadcastMessage fromString(String str) throws Exception {
    	
    	String[] splitted = str.split(":", 2);
    	
    	Type type = Type.valueOf(splitted[0].trim());
    	
    	if (type == Type.NEWUSER) {
    		if (splitted.length != 2)
    			throw new Exception("Invalid string");
    		
    		String pseudo = splitted[1].trim();
    		
    		return new BroadcastMessage(type, pseudo);
    	}
    	
    	return new BroadcastMessage(type);
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
