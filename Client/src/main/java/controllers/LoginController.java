package controllers;

import client.Application;
import client.ClientSide;
import hash.Hashing;
import client.MessageSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Variables;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Text loginStatus;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameField;
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
    @Setter
    public boolean maximized = false;
    private final Logger logger = LogManager.getLogger(this.getClass());
    @Getter
    private final Stage window;
    @Getter
    private final ClientSide clientSide;
    private final MessageSender messageSender;
    private JSONObject jsonObject;
    @Getter
    private RegisterController registerController;
    @Getter
    private Hashing hashing;

    public LoginController(Stage window) {
        this.window = window;
        Thread clientSideHandlerThread = new Thread(clientSide = new ClientSide(this));
        clientSideHandlerThread.setDaemon(true);
        clientSideHandlerThread.start();
        messageSender = clientSide.getMessageSender();
        logger.info("Thread for communication with server was created");
    }

    @FXML
    public void loginOnAction() throws JSONException { //TODO: ZMENIT ALE FAKT
        if (usernameField.getText() != null) {
            hashing = new Hashing();
            jsonObject = new JSONObject()
                    .put(Variables.ID, Variables.LOGIN_OF_USER)
                    .put(Variables.DATA, new JSONObject()
                            .put(Variables.USERNAME, usernameField.getText()));
            messageSender.printWriter(jsonObject);
        }
    }

    public void loginUser() {
        if (usernameField.getText() != null || passwordField.getText() != null) { //TODO: ZMENIT ALE FAKT
            jsonObject = new JSONObject()
                    .put(Variables.ID, Variables.LOGIN_OF_USER) //Login of User
                    .put(Variables.DATA, new JSONObject()
                            .put(Variables.USERNAME, usernameField.getText())
                            .put(Variables.PASSWORD, hashing.hashIt(passwordField.getText())));
            messageSender.printWriter(jsonObject);
        }
    }

    public void loginStatus(String message, String color) {
        loginStatus.setFill(Paint.valueOf(color));
        loginStatus.setText(message);
    }

    @FXML
    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        registerController = new RegisterController(this, messageSender, window);
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/registerScene.fxml"));
        loader.setController(registerController);
        Parent registerScene = loader.load();
        logger.info("Set scene to registerScene");
        getWindow().getScene().setRoot(registerScene);
        logger.info("Show window of app");
    }

    public void backToLoginScene(LoginController loginController) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/loginScene.fxml"));
        loader.setController(loginController);
        Parent loginScene = loader.load();
        logger.info("Set scene to loginScene");
        getWindow().getScene().setRoot(loginScene);
        logger.info("Show window of app");
    }

    public void chatScene() throws IOException {
        ChatController chatController = new ChatController(messageSender, window);
        clientSide.setChatController(chatController);
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/chatScene.fxml"));
        loader.setController(chatController);
        Parent chatScene = loader.load();
        logger.info("Set scene to chatScene");
        getWindow().getScene().setRoot(chatScene);
        logger.info("Show window of app");
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
            setMaximized(false);
        }
        else {
            stage.setMaximized(true);
            setMaximized(true);
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
