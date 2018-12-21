package org.etudinsa.clavardage.ui.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.sessions.SessionObserver;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserManager;
import org.etudinsa.clavardage.users.UserObserver;

import java.io.IOException;

class HomeStage extends Stage implements SessionObserver, UserObserver {

    final HomeController homeController;

    HomeStage() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/home.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 275);

        setTitle("Home");
        setScene(scene);

        homeController = loader.getController();

        SessionManager.getInstance().registerSessionObserver(this);
        UserManager.getInstance().registerUserObserver(this);
    }

    public void refreshUserList() {
        homeController.setUserDB(UserManager.getInstance().getUserDB());
    }

    @Override
    public void close() {
        super.close();

        try {
            UserManager.getInstance().leaveNetwork();
            SessionManager.getInstance().stop();
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
