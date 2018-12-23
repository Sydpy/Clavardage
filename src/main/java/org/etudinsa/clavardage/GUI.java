package org.etudinsa.clavardage;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.etudinsa.clavardage.gui.HomeStage;
import org.etudinsa.clavardage.gui.LoginStage;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.UserManager;

import java.io.IOException;
import java.util.Arrays;

public class GUI extends Application {

    private static UserManager userManager;
    private static SessionManager sessionManager;

    private static LoginStage loginStage;
    private static HomeStage homeStage;

    public static void main(String[] args) {

        ManagerFactory managerFactory;

        if (Arrays.asList(args).contains("--mock")) {
            managerFactory = new ManagerFactory(true);
        } else {
            managerFactory = new ManagerFactory(false);
        }

        userManager = managerFactory.getUserManager();
        sessionManager = managerFactory.getSessionManager();


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

    public static LoginStage getLoginStage() {
        return loginStage;
    }
    public static HomeStage getHomeStage() {
        return homeStage;
    }

    public static UserManager getUserManager() {
        return userManager;
    }
    public static SessionManager getSessionManager() {
        return sessionManager;
    }
}
