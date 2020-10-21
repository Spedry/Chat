package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class HBoxController {
    @FXML
    private Label userNameHBox;
    @FXML
    private Label userMessageHBox;
    @FXML
    private ImageView imgHBox;

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameHBox.setText("test");
        userMessageHBox.setText("msg");
    }*/

    public void setContent(String userName, String userMessage, String URL) {
        // set text from another class
        userNameHBox.setText(userName);
        userMessageHBox.setText(userMessage);
    }
}
