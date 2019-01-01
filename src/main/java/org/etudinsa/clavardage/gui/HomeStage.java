package org.etudinsa.clavardage.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.etudinsa.clavardage.GUI;
import org.etudinsa.clavardage.users.User;

import java.io.IOException;

public class HomeStage extends Stage {

    @FXML
    public ListView userListView;

    @FXML
    public TabPane tabPane;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    public HomeStage() throws IOException {
        super();
        initModality(Modality.NONE);
        initStyle(StageStyle.DECORATED);

        // Load FXML and set scene
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/home.fxml"));
        loader.setController(this);

        Scene scene = new Scene((Parent) loader.load());
        setScene(scene);

        userListView.setItems(userList);
        userListView.setCellFactory(userListView -> new UserCell());
    }
}
