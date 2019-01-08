package org.etudinsa.clavardage.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.etudinsa.clavardage.GUI;
import org.etudinsa.clavardage.sessions.Message;
import org.etudinsa.clavardage.users.User;

import java.io.IOException;

public class MessageCell extends ListCell<Message> {

    @FXML
    public VBox vBox;
    @FXML
    public Text content;
    @FXML
    public Text date;

    private FXMLLoader loader;

    private Message message;

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        this.message = message;

        if(empty || message == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/messagecell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            User user = GUI.getUserManager().getUserByIp(message.getDistantIP());

            if (message.isSent()) {
                content.setText("Me : " + message.getContent().getContent().toString());
            } else {
                content.setText(user.pseudo + " : " + message.getContent().getContent().toString());
            }

            date.setText(message.getContent().getDate().toString());

            setText(null);
            setGraphic(vBox);
        }
    }
}
