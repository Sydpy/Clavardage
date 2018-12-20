package org.etudinsa.clavardage.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import org.etudinsa.clavardage.users.User;

import java.io.IOException;

public class UserCell extends ListCell<User> {

    @FXML
    public Label userLabel;
    @FXML
    public FlowPane flowPane;

    private FXMLLoader loader;

    private User user;

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        this.user = user;

        if(empty || user == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getClassLoader().getResource("usercell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            userLabel.setText(user.pseudo);

            setText(null);
            setGraphic(flowPane);
        }
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);

        if(user != null && selected) {
            GUI.getHomeStage().homeController.openChat(user);
        }
    }


}
