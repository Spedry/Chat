package sk.Spedry.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    public PasswordField passwordField;
    public TextField usernameField;
    public AnchorPane loginAnchor;

    public void loginOnAction(ActionEvent actionEvent) {

    }

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/registerPopup.fxml"));
        Scene scene = new Scene(register);
        Stage widnow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        widnow.setScene(scene);
        widnow.show();
    }
}
