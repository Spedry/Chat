package controllers;

import client.Application;
import client.MessageSender;
import controllers.list.Room;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Variables;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PopupController {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private Stage popup;
    @FXML
    public TextField nameOfRoom;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button createOrLoginButton;
    @FXML
    public Text roomStatus;
    @FXML
    public Label whatRoom;
    @FXML
    public Button closeButton;
    private final MessageSender messageSender;
    private final ChatController chatController;
    @Setter
    private String id;

    public PopupController(Stage popup, MessageSender messageSender, ChatController chatController) {
        this.popup = popup;
        popup.initStyle(StageStyle.TRANSPARENT);
        this.messageSender = messageSender;
        this.chatController = chatController;
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
        if (nameOfButton.equals("Login"))
            whatRoom.setText("Room login");
        else
            whatRoom.setText("Create room");
        createOrLoginButton.setText(nameOfButton);
        popup.setScene(newScene);
    }

    /*@FXML
    public void closeStage(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }*/

    @FXML
    public void closeStage(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // popupHBox.fxml ->
    @FXML
    public void createOrLoginOnAction() {
        String name = nameOfRoom.getText();
        boolean createNewButton = true;
        if (name != null || passwordField != null) {
            for (Room room : chatController.getRoomObservableList()) {
                if (room.getName().equals(name)) {
                    createNewButton = false;
                    roomStatus("Room is already loaded", "Red");
                }
            }
            if (createNewButton) {
                JSONObject jsonObject = new JSONObject()
                        .put(Variables.ID, id)
                        .put(Variables.DATA, new JSONObject()
                                .put(Variables.ROOM, name)
                                .put(Variables.PASSWORD, passwordField.getText()));
                messageSender.printWriter(jsonObject);
            }
        }
    }

    public void roomStatus(String message, String color) {
        roomStatus.setFill(Paint.valueOf(color));
        roomStatus.setText(message);
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