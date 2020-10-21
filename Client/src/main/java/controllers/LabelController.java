package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LabelController {
    @FXML
    public Label userName;

    public void setContent(String userName) {
        this.userName.setText(userName);
    }
}
