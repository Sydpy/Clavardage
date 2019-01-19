package org.etudinsa.clavardage.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.etudinsa.clavardage.GUI;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;

public class LoginStage extends Stage {

    @FXML
    public Button joinButton;

    @FXML
    public TextField username;

    @FXML
    public Text error;

    public LoginStage() throws Exception {
        super();
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.DECORATED);

        // Load FXML and set scene
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/login.fxml"));
        loader.setController(this);

        Scene scene = new Scene((Parent) loader.load());
        setScene(scene);

        // Set button handler
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
        rng.setSeed(new Date().getTime());
        keyPairGenerator.initialize(1024, rng);

        joinButton.setOnMouseClicked(mouseEvent -> {

            try {

                GUI.getUserManager().joinNetwork(username.getText(), keyPairGenerator.generateKeyPair());
                GUI.getSessionManager().startListening();
                close();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText("Try again");
                alert.showAndWait();
                e.printStackTrace();
            }

        });
    }

}
