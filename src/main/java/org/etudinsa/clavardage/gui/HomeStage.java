package org.etudinsa.clavardage.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.etudinsa.clavardage.GUI;
import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.SessionObserver;
import org.etudinsa.clavardage.users.User;
import org.etudinsa.clavardage.users.UserObserver;

import java.io.IOException;

public class HomeStage extends Stage implements UserObserver, SessionObserver {

    @FXML
    public ListView userListView;

    @FXML
    public TabPane tabPane;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    public HomeStage() throws IOException {
        super();
        initModality(Modality.NONE);
        initStyle(StageStyle.DECORATED);

        GUI.getSessionManager().registerSessionObserver(this);
        GUI.getUserManager().registerUserObserver(this);

        // Load FXML and set scene
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/home.fxml"));
        loader.setController(this);

        Scene scene = new Scene((Parent) loader.load());
        setScene(scene);

        //Setup userListView
        userListView.setItems(userList);
        userListView.setCellFactory(userListView -> new UserCell());
    }

    public void selectChat(User user) throws Exception {

        FilteredList<Tab> filtered = tabPane.getTabs().filtered(tab -> ((ChatTab) tab).getUser().equals(user));

        ChatTab tab;
        if (filtered.isEmpty()) {
            tab = new ChatTab(user);
            tabPane.getTabs().add(tab);
        } else {
            tab = (ChatTab) filtered.get(0);
        }

        tabPane.getSelectionModel().select(tab);
    }

    public void refreshUserList() {
        userList.clear();
        userList.addAll(GUI.getUserManager().getUserDB());
    }

    private ChatTab getTabByUser(User user) {
        for (Tab tab : tabPane.getTabs()) {
            if (user.equals(((ChatTab) tab).getUser()))
                return (ChatTab) tab;
        }

        return null;
    }

    private void newMessage(Message message) {
        User user = GUI.getUserManager().getUserByIp(message.getDistantIP());

        if (user == null) return;

        ChatTab tab = getTabByUser(user);

        if (tab == null) return;

        tab.newMessage(message);
    }

    @Override
    public void messageSent(Message message) {
        newMessage(message);
    }

    @Override
    public void messageReceived(Message message) {
        newMessage(message);
    }

    @Override
    public void newUser(User newUser) {
        refreshUserList();
    }

    @Override
    public void userLeaving(User userLeaving) {
        refreshUserList();
    }
    
    @Override
    public void updatedUserList() {
    	refreshUserList();
    }
}
