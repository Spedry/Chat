package controllers;

import client.Application;
import client.ClientSide;
import Hashing.Hashing;
import client.MessageSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {

    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameField;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final Stage window;
    @Getter
    private final ClientSide clientSide;
    private final MessageSender messageSender;
    private JSONObject jsonObject;
    @Getter
    private RegisterController registerController;
    @Getter
    private Hashing hashing;
    private final String   data = "Data", userName = "Username", hash = "Password", message = "Message",
            messagefromUser = "MfU", showLoginofUser = "SLoU";
    private final String ioexception = "Reading a network file and got disconnected.\n" +
            "Reading a local file that was no longer available.\n" +
            "Using some stream to read data and some other process closed the stream.\n" +
            "Trying to read/write a file, but don't have permission.\n" +
            "Trying to write to a file, but disk space was no longer available.\n" +
            "There are many more examples, but these are the most common, in my experience.";
    private boolean login = true;

    public LoginController(Stage window) {
        this.window = window;
        Thread clientSideHandlerThread = new Thread(clientSide = new ClientSide(this));
        clientSideHandlerThread.setDaemon(true);
        clientSideHandlerThread.start();
        messageSender = clientSide.getMessageSender();
        logger.info("Thread for communication with server was created");
    }

    @FXML
    public void loginOnAction() throws JSONException {
        if (usernameField.getText() != null) {
            hashing = new Hashing();
            jsonObject = new JSONObject()
                    .put("ID", "LoU")
                    .put("Data", new JSONObject()
                            .put("Username", usernameField.getText()));
            messageSender.printWriter(jsonObject);
        }
    }

    public void loginUser() {
        if (usernameField.getText() != null || passwordField.getText() != null) {
            jsonObject = new JSONObject()
                    .put("ID", "LoU") //Login of User
                    .put("Data", new JSONObject()
                            .put("Username", usernameField.getText())
                            .put("Password", hashing.hashIt(passwordField.getText())));
            messageSender.printWriter(jsonObject);
        }
    }

    @FXML
    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        registerController = new RegisterController(this, messageSender);
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/registerScene.fxml"));
        loader.setController(registerController);
        Parent registerScene = loader.load();
        Scene scene = new Scene(registerScene);
        window.setScene(scene);
        logger.info("Set scene to registerScene");
        window.show();
        logger.info("Show window of app");
    }

    public void backToLoginScene(LoginController loginController) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/loginScene.fxml"));
        loader.setController(loginController);
        Parent loginScene = loader.load();
        Scene scene = new Scene(loginScene);
        window.setScene(scene);
        logger.info("Set scene to loginScene");
        window.show();
        logger.info("Show window of app");
    }

    public void chatScene() throws IOException {
        ChatController chatController = new ChatController(messageSender, window);
        clientSide.setChatController(chatController);
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/chatScene.fxml"));
        loader.setController(chatController);
        Parent chatScene = loader.load();
        Scene scene = new Scene(chatScene);
        window.setScene(scene);
        logger.info("Set scene to chatScene");
        window.show();
        logger.info("Show window of app");
    }
}
