package controllers;

import client.Application;
import client.MessageSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Variables;

import java.io.IOException;

public class PopupController {

    private static final Logger logger = LogManager.getLogger(Application.class);
    private Stage popup;
    @FXML
    public TextField nameOfRoom;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button createOrLoginButton;
    private final MessageSender messageSender;
    @Setter
    private String id;

    public PopupController(Stage popup, MessageSender messageSender) {
        this.popup = popup;
        this.messageSender = messageSender;
    }

    // popup.fxml ->
    @FXML
    public void loginIntoExistingRoomOnAction(ActionEvent actionEvent) {
        logger.info("Login into existing room");
        setId(Variables.LOAD_INTO_THE_ROOM);
        createPopup("Login");
    }

    @FXML
    public void createNewRoomOnAction(ActionEvent actionEvent) {
        logger.info("Creating new room");
        setId(Variables.CREATE_NEW_ROOM);
        createPopup("Create");
    }

    private void createPopup(String nameOfButton) {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/popupHBox.fxml"));
        loader.setController(this);
        Scene newScene = null;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ioe) {
            logger.error(Variables.IOEXCEPTION, ioe);
        }
        createOrLoginButton.setText(nameOfButton);
        popup.setScene(newScene);
    }

    // popupHBox.fxml ->
    @FXML
    public void createOrLoginOnAction() {
        if (nameOfRoom.getText() != null || passwordField != null) {
            JSONObject jsonObject = new JSONObject()
                    .put(Variables.ID, id)
                    .put(Variables.DATA, new JSONObject()
                            .put(Variables.ROOM, nameOfRoom.getText())
                            .put(Variables.PASSWORD, passwordField.getText()));
            messageSender.printWriter(jsonObject);
        }
    }

    @FXML
    public void backButtonOnAction() {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/popup.fxml"));
        loader.setController(this);
        Scene newScene = null;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ioe) {
            logger.error(Variables.IOEXCEPTION, ioe);
        }
        popup.setScene(newScene);
    }
}