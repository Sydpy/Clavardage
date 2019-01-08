package org.etudinsa.clavardage.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.etudinsa.clavardage.GUI;
import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.sessions.Session;
import org.etudinsa.clavardage.users.User;

import java.io.IOException;
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

    private FXMLLoader loader;

    ChatTab(User user) throws Exception {
        super(user.pseudo);

        this.user = user;
        this.session = GUI.getSessionManager().getSessionByDistantUserPseudo(user.pseudo);

        this.loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/chat.fxml"));

        try {
            this.loader.setController(this);
            setContent(this.loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageList.setAll(session.getMessages());
        messageListView.setItems(messageList);
        messageListView.setCellFactory(listView -> new MessageCell());
    }

    public void sendMessage(MouseEvent mouseEvent) {
        try {
            GUI.getSessionManager().sendMessage(messageTextArea.getText(), user.pseudo);
            messageTextArea.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void newMessage(Message message) {
        messageList.add(message);
    }
}
