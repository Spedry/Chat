package sk.Spedry.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import sk.Spedry.gui.App;
import sk.Spedry.gui.Client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class RegisterController {

    public PasswordField repeatField;
    public PasswordField passwordField;
    public TextField usernameField;
    public Text errcesMessage;
    public static String jsonObject = null;
    Client client = Client.getInstance(); //to get access to the Client class what was created in class App

    public void registerOnAction(ActionEvent actionEvent) throws JSONException {
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
}
