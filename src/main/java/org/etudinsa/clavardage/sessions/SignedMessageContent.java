package org.etudinsa.clavardage.sessions;

import java.io.Serializable;

public class SignedMessageContent implements Serializable {

    public final MessageContent content;
    public final String contentSignature;

    public SignedMessageContent(MessageContent content, String contentSignature) {
        this.content = content;
        this.contentSignature = contentSignature;
    }
}
