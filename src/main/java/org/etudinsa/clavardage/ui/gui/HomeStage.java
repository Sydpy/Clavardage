package org.etudinsa.clavardage.ui.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.etudinsa.clavardage.sessions.SessionManager;
import org.etudinsa.clavardage.users.UserManager;

import java.io.IOException;

class HomeStage extends Stage {

    final HomeController homeController;

    HomeStage() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("home.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 275);

        setTitle("Home");
        setScene(scene);

        homeController = loader.getController();
    }

    public void refreshUserList() {
        //homeController.setUserDB(UserManager.getInstance().createMockUserDB(10));
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
}
