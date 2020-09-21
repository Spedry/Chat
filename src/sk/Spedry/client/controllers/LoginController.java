package sk.Spedry.client.controllers;

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
import sk.Spedry.client.Client;
import java.io.IOException;

public class LoginController {

    public PasswordField passwordField;
    public TextField usernameField;
    public static String jsonObject = null;
    Client client = Client.getInstance(); //to get access to the Client class what was created in class App

    public void loginOnAction() throws JSONException {
        if (usernameField.getText() != null || passwordField.getText() != null)
            jsonObject = new JSONObject()
                    .put("ID", "LoU") //Login of User
                    .put("Data", new JSONObject()
                            .put("Username", usernameField.getText())
                            .put("Password", passwordField.getText())) //nahradiť za hash
                            .toString();
        client.setInput(jsonObject);
        //setUserMessege(new BufferedReader(new StringReader(jsonObject)));
        //App.getClient().getPrintWriter().println(jsonObject);
        /*try (PrintWriter out = App.out) {
            out.write(jsonObject);
        }*/
        //App.client.out(jsonObject);
    }

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/registerScene.fxml"));
        Scene scene = new Scene(register);
        Stage widnow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        widnow.setScene(scene);
        widnow.show();
    }
}
