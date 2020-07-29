package sk.Spedry.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class RegisterController {

    public PasswordField repeatField;
    public PasswordField passwordField;
    public TextField usernameField;
    public Text errcesMessage;

    public void registerOnAction(ActionEvent actionEvent) throws JSONException {
        if(passwordField.getText().equals(repeatField.getText())) { //nahradiť za hash
            //pridať odpoveď servera na už existujúce username
            //pridať hashing
            errces("Succes", "green");
            String jsonString = new JSONObject()
                    .put("ID", "RoNU") //Registration of New User
                    .put("Data", new JSONObject()
                            .put("username", usernameField.getText())
                            .put("password", passwordField.getText())) //nahradiť za hash
                    .toString();

            System.out.println(jsonString);
        }
        else {
            errces("Passwords do not match", "red");
        }
    }

    public void errces(String message, String color) {
        errcesMessage.setText(message);
        errcesMessage.setFill(Paint.valueOf(color));
    }
}
