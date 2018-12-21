package org.etudinsa.clavardage.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.etudinsa.clavardage.GUI;
import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.SessionObserver;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserObserver;

import java.io.IOException;

public class HomeStage extends Stage implements SessionObserver, UserObserver {

    final HomeController homeController;

    public HomeStage() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/home.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 275);

        setTitle("Home");
        setScene(scene);

        homeController = loader.getController();

        GUI.getSessionManager().registerSessionObserver(this);
        GUI.getUserManager().registerUserObserver(this);
    }

    public void refreshUserList() {
        homeController.setUserDB(GUI.getUserManager().getUserDB());
    }

    @Override
    public void close() {
        super.close();

        try {
            GUI.getUserManager().leaveNetwork();
            GUI.getSessionManager().stopListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageSent(Message message) {
        //TODO
    }

    @Override
    public void messageReceived(Message message) {
        //TODO
    }

    @Override
    public void newUser(User newUser) {
        //TODO
    }

    @Override
    public void userLeaving(User userLeaving) {
        //TODO
    }
}
