package org.etudinsa.clavardage.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.LANUserManager;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;

public class LoginController {

    private KeyPairGenerator keyPairGenerator;

    private LANUserManager userManager = LANUserManager.getInstance();
    private SessionManager sessionManager = SessionManager.getInstance();

    public LoginController() throws NoSuchAlgorithmException, NoSuchProviderException {
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyPairGenerator.initialize(1024, rng);
    }

    @FXML
    public TextField username;

    @FXML
    public Text error;

    public void joinNetwork(MouseEvent mouseEvent) {

        try {
            userManager.joinNetwork(username.getText(), keyPairGenerator.generateKeyPair());
        } catch (Exception e) {
            error.setText(e.toString());
            return;
        }

        sessionManager.start();

        GUI.getLoginStage().close();
        GUI.getHomeStage().show();
        GUI.getHomeStage().refreshUserList();
    }
}
