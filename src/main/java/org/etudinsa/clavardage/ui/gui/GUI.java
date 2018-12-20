package org.etudinsa.clavardage.ui.gui;

import javafx.application.Application;
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

        loginStage.show();
    }

    static LoginStage getLoginStage() {
        return loginStage;
    }

    static HomeStage getHomeStage() {
        return homeStage;
    }
}
