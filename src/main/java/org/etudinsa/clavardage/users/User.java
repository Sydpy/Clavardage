package org.etudinsa.clavardage.users;

import java.io.Serializable;
import java.net.InetAddress;
import java.security.*;
import java.util.Objects;

public class User implements Serializable {

    public final String pseudo;
    public final InetAddress ip;
    public final PublicKey publicKey;

    User(String pseudo, InetAddress ip, PublicKey publicKey) {
        this.pseudo = pseudo;
        this.ip = ip;
        this.publicKey = publicKey;
    }

    public boolean verifySig(byte[] data, byte[] sig)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initVerify(publicKey);
        signer.update(data);
        return (signer.verify(sig));
    }
    
    @Override
    public String toString() {
    	return this.pseudo + " " + this.ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(pseudo, user.pseudo) &&
                Objects.equals(ip, user.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo, ip);
    }
}
