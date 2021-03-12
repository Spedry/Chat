package controllers;

import client.MessageSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import hash.Hashing;
import org.json.JSONException;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Variables;

import java.io.IOException;

public class RegisterController {
    @FXML
    public PasswordField repeatField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameField;
    @FXML
    public Text errcesMessage;
    private JSONObject jsonObject = null;
    private MessageSender messageSender;
    private final Logger logger = LogManager.getLogger(this.getClass());
    @Getter
    private Hashing hashing;
    private LoginController loginController;


    public RegisterController(LoginController loginController, MessageSender messageSender) {
        this.loginController = loginController;
        this.messageSender = messageSender;
    }

    @FXML
    public void registerOnAction() throws JSONException {
        hashing = new Hashing();
        jsonObject = new JSONObject()
                .put(Variables.ID, Variables.REGISTRATION_OF_NEW_USER);
        messageSender.printWriter(jsonObject);
    }

    public void registerUser() {
        if(passwordField.getText().equals(repeatField.getText())) {
            messageSuccess("Succes", "green");
            logger.info("Creating jsonobject");
            jsonObject = new JSONObject()
                    .put(Variables.ID, Variables.REGISTRATION_OF_NEW_USER) //Registration of New User
                    .put(Variables.DATA, new JSONObject()
                            .put(Variables.USERNAME, usernameField.getText())
                            .put(Variables.PASSWORD, hashing.hashIt(passwordField.getText())));
            logger.info("Jsonobject was created");
            logger.info(jsonObject);
        }
        else {
            logger.warn("Passwords do not match");
            messageSuccess("Passwords do not match", "red");
        }
        logger.info("Sending data to server");
        messageSender.printWriter(jsonObject);
    }

    public void messageSuccess(String message, String color) {
        errcesMessage.setText(message);
        errcesMessage.setFill(Paint.valueOf(color));
    }
    @FXML
    public void backChangeSceneOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScene.fxml"));
        loader.setController(loginController);
        Parent registerScene = loader.load();
        Scene scene = new Scene(registerScene);
        Stage widnow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        widnow.setScene(scene);
        widnow.show();
    }
}
