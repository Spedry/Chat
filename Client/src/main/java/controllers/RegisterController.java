package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import client.Client;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterController {

    public PasswordField repeatField;
    public PasswordField passwordField;
    public TextField usernameField;
    public Text errcesMessage;
    public static String jsonObject = null;
    Client client = Client.getInstance(); //to get access to the Client class what was created in class App
    private final Logger logger = LogManager.getLogger(this.getClass());

    public void registerOnAction() throws JSONException {
        if(passwordField.getText().equals(repeatField.getText())) { //nahradiť za hash
            //pridať odpoveď servera na už existujúce username
            //pridať hashing
            errces("Succes", "green");
            jsonObject = new JSONObject()
                    .put("ID", "RoNU") //Registration of New User
                    .put("Data", new JSONObject()
                            .put("Username", usernameField.getText())
                            .put("Password", passwordField.getText())) //nahradiť za hash
                            .toString();
            System.out.println(jsonObject);
        }
        else {
            errces("Passwords do not match", "red");
        }
        client.setInput(jsonObject);
    }

    public void errces(String message, String color) {
        errcesMessage.setText(message);
        errcesMessage.setFill(Paint.valueOf(color));
    }

    public void backChangeSceeneOnAction(ActionEvent actionEvent) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/fxml/loginScene.fxml"));
        Scene scene = new Scene(register);
        Stage widnow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        widnow.setScene(scene);
        widnow.show();
    }
}
