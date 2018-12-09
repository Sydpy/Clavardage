package org.etudinsa.clavardage.pseudos;

public class PseudoManager {

    private static PseudoManager instance = new PseudoManager();

    public static PseudoManager getInstance() {
        return instance;
    }

    private PseudoManager() {
    }
}
