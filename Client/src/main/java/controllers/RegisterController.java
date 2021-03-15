package controllers;

import client.MessageSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    public PasswordField repeatField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameField;
    @FXML
    public Text registerStatus;
    @FXML
    public Button minimize;
    @FXML
    public Button maximize;
    @FXML
    public Button close;
    @FXML
    public ImageView drag;
    private double xOffset = 0;
    private double yOffset = 0;
    private JSONObject jsonObject = null;
    private MessageSender messageSender;
    private final Logger logger = LogManager.getLogger(this.getClass());
    @Getter
    private Hashing hashing;
    private LoginController loginController;
    private Stage window;


    public RegisterController(LoginController loginController, MessageSender messageSender, Stage window) {
        this.loginController = loginController;
        this.messageSender = messageSender;
        this.window = window;
    }

    @FXML
    public void registerOnAction() throws JSONException {
        if(passwordField.getText().equals(repeatField.getText())) {
            hashing = new Hashing();
            jsonObject = new JSONObject()
                    .put(Variables.ID, Variables.REGISTRATION_OF_NEW_USER);
            messageSender.printWriter(jsonObject);
        }
    }

    public void registerUser() {
        if(passwordField.getText().equals(repeatField.getText())) {
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
            registrationStatus("Passwords do not match", "red");
        }
        logger.info("Sending data to server");
        messageSender.printWriter(jsonObject);
    }

    public void registrationStatus(String message, String color) {
        registerStatus.setFill(Paint.valueOf(color));
        registerStatus.setText(message);
    }
    @FXML
    public void backChangeSceneOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScene.fxml"));
        loader.setController(loginController);
        Parent registerScene = loader.load();
        loginController.getWindow().getScene().setRoot(registerScene);
    }

    @FXML
    public void minimizeOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void maximizeOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) maximize.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            loginController.setMaximized(false);
        }
        else {
            stage.setMaximized(true);
            loginController.setMaximized(true);
        }
    }

    @FXML
    public void closeOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drag.setOnMousePressed(event -> {
            xOffset = window.getX() - event.getScreenX();
            yOffset = window.getY() - event.getScreenY();
        });

        drag.setOnMouseDragged(event -> {
            window.setX(event.getScreenX() + xOffset);
            window.setY(event.getScreenY() + yOffset);
        });
    }
}
