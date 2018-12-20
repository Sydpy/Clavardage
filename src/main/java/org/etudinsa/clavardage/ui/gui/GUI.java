package org.etudinsa.clavardage.ui.gui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    private static LoginStage loginStage;
    private static HomeStage homeStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        loginStage = new LoginStage();
        homeStage = new HomeStage();
        
        loginStage.getIcons().add(new Image("https://cdn3.iconfinder.com/data/icons/badger-s-christmas/300/mail-512.png"));
        homeStage.getIcons().add(new Image("https://cdn3.iconfinder.com/data/icons/badger-s-christmas/300/mail-512.png"));

        loginStage.show();
    }

    static LoginStage getLoginStage() {
        return loginStage;
    }

    static HomeStage getHomeStage() {
        return homeStage;
    }
}
