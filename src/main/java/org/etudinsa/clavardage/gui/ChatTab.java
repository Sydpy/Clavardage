package org.etudinsa.clavardage.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.etudinsa.clavardage.GUI;
import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.Session;
import org.etudinsa.clavardage.users.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatTab extends Tab implements Initializable {

    @FXML
    public ListView messageListView;

    @FXML
    public TextArea messageTextArea;

    private ObservableList<Message> messageList = FXCollections.observableArrayList();

    private final User user;
    private final Session session;

    ChatTab(User user) throws Exception {
        super(user.pseudo);

        this.user = user;
        this.session = GUI.getSessionManager().getSessionByDistantUserPseudo(user.pseudo);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        messageList.setAll(session.getMessages());

        messageListView.setItems(messageList);
    }

    public void sendMessage(MouseEvent mouseEvent) {
        try {
            GUI.getSessionManager().sendMessage(messageTextArea.getText(), user.pseudo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
