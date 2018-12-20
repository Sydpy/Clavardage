package org.etudinsa.clavardage.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import org.etudinsa.clavardage.users.User;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    public ListView userListView;

    @FXML
    public TabPane tabs;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userListView.setItems(userList);
        userListView.setCellFactory(userListView -> new UserCell());
    }

    public void setUserDB(User[] userDB) {
        userList.clear();
        userList.addAll(Arrays.asList(userDB));
    }
}
