package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class MyUser extends User {

    public final PrivateKey privateKey;

    MyUser(String pseudo, InetAddress ip, KeyPair keyPair) {
        super(pseudo, ip, keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
    }

    public String signObject(Object object) throws Exception {

        byte[] data = toByteArray(object);

        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(privateKey);
        signer.update(data);

        return Base64.getMimeEncoder().encodeToString(signer.sign());
    }
}
