package controllers;

import client.App;
import client.Client;
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
import client.PassHash;
import org.json.JSONException;
import org.json.JSONObject;
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
    public static String jsonObject = null;
    private Client client;
    private final Logger logger = LogManager.getLogger(this.getClass());
    @Getter
    private PassHash passHash;
    private LoginController loginController;


    public RegisterController(LoginController loginController, Client client) {
        this.loginController = loginController;
        this.client = client;
    }

    @FXML
    public void registerOnAction() throws JSONException {
        passHash = new PassHash();
        jsonObject = new JSONObject()
                .put("ID", "RoNU")
                .toString();
        client.setInput(jsonObject);
    }

    public void registerUser() {
        if(passwordField.getText().equals(repeatField.getText())) {
            //pridať odpoveď servera na už existujúce username
            //pridať hashing
            messageSuccess("Succes", "green");
            logger.info("Creating jsonobject");
            jsonObject = new JSONObject()
                    .put("ID", "RoNU") //Registration of New User
                    .put("Data", new JSONObject()
                            .put("Username", usernameField.getText())
                            .put("Password", passHash.hashIt(passwordField.getText())))
                    .toString();
            logger.info("Jsonobject was created");
            logger.info(jsonObject);
        }
        else {
            logger.warn("Passwords do not match");
            messageSuccess("Passwords do not match", "red");
        }
        logger.info("Sending data to server");
        client.setInput(jsonObject);
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
