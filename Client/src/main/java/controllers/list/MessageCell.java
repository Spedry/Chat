package controllers.list;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MessageCell extends ListCell<Message> {
    @FXML
    public VBox messageCell;
    @FXML
    public Label userName;
    @FXML
    public Label userMessage;
    private FXMLLoader loader;

    public MessageCell() {
        super();
        loader = new FXMLLoader(getClass().getResource("/fxml/messageCell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        setGraphic(null);

        if (message != null && !empty) {

            userName.setText(message.getName());
            userMessage.setText(message.getMessage());
            setGraphic(messageCell);
        }
    }
}
