package org.etudinsa.clavardage.ui.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginStage extends Stage {

    public LoginStage() throws IOException {

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/login.fxml"));

        Scene scene = new Scene(root, 300, 275);

        setTitle("Login");
        setScene(scene);
    }
}
