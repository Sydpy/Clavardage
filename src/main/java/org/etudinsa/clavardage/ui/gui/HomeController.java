package org.etudinsa.clavardage.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.etudinsa.clavardage.users.User;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class HomeController implements Initializable {

    @FXML
    public ListView userListView;

    @FXML
    public TabPane tabPane;

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

    public void openChat(User user) {

        FilteredList<Tab> filtered = tabPane.getTabs().filtered(tab -> tab.getText().equals(user.pseudo));

        Tab tab;
        if (filtered.isEmpty()) {
            tab = new Tab(user.pseudo);
            tabPane.getTabs().add(tab);
        } else {
            tab = filtered.get(0);
        }

        int index = tabPane.getTabs().indexOf(tab);
        tabPane.getSelectionModel().clearAndSelect(index);
    }
}
