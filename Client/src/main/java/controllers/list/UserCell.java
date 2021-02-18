package controllers.list;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class UserCell extends ListCell<String> {
    @FXML
    public Label userName;
    private FXMLLoader loader;

    public UserCell() {
        super();

        loader = new FXMLLoader(getClass().getResource("/fxml/userCell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(String user, boolean empty) {
        super.updateItem(user, empty);
        setGraphic(null);

        if (user != null && !empty) {
            userName.setText(user);
            setGraphic(userName);
        }
    }
}
