package org.etudinsa.clavardage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.etudinsa.clavardage.gui.HomeStage;
import org.etudinsa.clavardage.gui.LoginStage;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.UserManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class GUI extends Application {

    private static UserManager userManager;
    private static SessionManager sessionManager;

    private static HomeStage homeStage;

    public static void main(String[] args) throws UnknownHostException {

        ManagerFactory managerFactory;

        if (Arrays.asList(args).contains("--mock")) {
            managerFactory = new ManagerFactory(true);
        } else if (args.length>1 && args[0].equals("server")){
            managerFactory = new ManagerFactory(InetAddress.getByName(args[1]));
            //managerFactory = new ManagerFactory(InetAddress.getLocalHost());
        } else {
            managerFactory =new ManagerFactory(false);
        }

        userManager = managerFactory.getUserManager();
        sessionManager = managerFactory.getSessionManager();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        LoginStage loginStage = new LoginStage();
        homeStage = new HomeStage();

        loginStage.getIcons().add(new Image("https://cdn3.iconfinder.com/data/icons/badger-s-christmas/300/mail-512.png"));
        homeStage.getIcons().add(new Image("https://cdn3.iconfinder.com/data/icons/badger-s-christmas/300/mail-512.png"));

        loginStage.showAndWait();

        // Check if the login was successful
        if (userManager.isConnected()) {
            homeStage.show();
            homeStage.refreshUserList();
            homeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    try {
                        userManager.leaveNetwork();
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            System.exit(1);
        }
    }

    public static UserManager getUserManager() {
        return userManager;
    }
    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    public static HomeStage getHomeStage() {
        return homeStage;
    }
}
