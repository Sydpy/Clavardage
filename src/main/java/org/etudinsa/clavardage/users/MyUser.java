package org.etudinsa.clavardage.users;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;

public class MyUser extends User {

    public final PrivateKey privateKey;

    MyUser(String pseudo, InetAddress ip, KeyPair keyPair) {
        super(pseudo, ip, keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
    }

    public byte[] signData(byte[] data) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(privateKey);
        signer.update(data);
        return (signer.sign());
    }
}
