package sk.Spedry.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginController {

    public PasswordField passwordField;
    public TextField usernameField;

    public void loginOnAction(ActionEvent actionEvent) throws JSONException {
        String jsonString = new JSONObject()
                .put("ID", "LoU") //Login of User
                .put("Data", new JSONObject()
                        .put("username", usernameField.getText())
                        .put("password", passwordField.getText())) //nahradiť za hash
                .toString();
    }

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/registerPopup.fxml"));
        Scene scene = new Scene(register);
        Stage widnow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        widnow.setScene(scene);
        widnow.show();
    }
}
