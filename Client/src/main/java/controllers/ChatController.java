package controllers;

import client.MessageSender;
import controllers.list.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Methods;
import shortcuts_for_M_and_V.Variables;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final Stage window;
    private final MessageSender messageSender;
    @Setter
    private int currentRoomID = 0;
    @FXML
    public Label nameofCurrentRoom;
    @FXML
    public ListView roomListView;
    @FXML
    public ListView onlineUsersListView;
    @FXML
    public ListView messageListView;
    @FXML
    public TextField messageField;
    @FXML
    public ScrollPane chatMessageScrollPane;
    @FXML
    public VBox chatBox;
    ObservableList<Room> roomObservableList = FXCollections.observableArrayList();
    ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
    ObservableList<String> onlineUsersObservableList = FXCollections.observableArrayList();

    public ChatController(MessageSender messageSender, Stage widnow) {
        this.messageSender = messageSender;
        this.window = widnow;
    }

    @FXML //initialized manually
    public void sendOnEnterPress() throws JSONException {
        // TODO: zapracovať createJson()
        logger.info("Sending message from room ID: " + currentRoomID);
        JSONObject jsonObject = new JSONObject()
                .put(Variables.ID, Variables.MESSAGE_FROM_USER) //message from User
                .put(Variables.DATA, new JSONObject()
                        .put(Variables.MESSAGE, messageField.getText())
                        .put(Variables.ROOM_ID, currentRoomID) );
        messageSender.printWriter(jsonObject);
        messageField.clear();
    }

    //TODO: funkcia kde si može užívateľ nastaviť lubovolnú room ako general
    @FXML
    public void goToHomeOnAction() { //LOAD ROOM GENERAL  => ID 0
        logger.info("goToHome button");
        messageSender.printWriter(Methods.createJson(Variables.LOAD_AN_EXISTING_ROOM, Variables.ROOM_ID, 0));
        if (currentRoomID != 0) {
            setCurrentRoomID(0);
            clearMessageListView();
        }
    }

    @FXML
    public void switchBetweenGroupAndFriendOnAction() {
        logger.info("switchBetweenGroupAndFriend button");
        // TODO: switch scény medzi skupinami a priateľmi
    }

    public void clearMessageListView() {
        messageListView.getItems().clear();
    }

    public void addRoomButton(int roomId, String roomName) {
        roomObservableList.add(new Room(roomId, roomName));
    }

    public void deleteRoomButton(int ID) {
        //TODO: THIS
    }

    public void setNameofCurrentRoom(String roomName) {
        nameofCurrentRoom.setText(roomName);
    }

    public void showOnlineUser(List<String> listofOnlineUsers) {
        onlineUsersObservableList.clear();
        for (String onlineUser : listofOnlineUsers) {
            onlineUsersObservableList.add(onlineUser);
        }
    }

    public void addOnlineUser(String onlineUser) {
        onlineUsersObservableList.add(onlineUser);
    }

    public void deleteOnlineUser(String onlineUser) {
        for (String user : onlineUsersObservableList) {
            if (user.equals(onlineUser)) {
                logger.info("User: " + user + " was removed");
                onlineUsersObservableList.remove(user);
                break;
            }
        }
    }

    public void showMessage(String userName, String userMessage) {
        messageObservableList.add(new Message(userName, userMessage));
        messageListView.scrollTo(messageListView.getItems().size() - 1);
    }

    private void createRoomListView() {
        roomObservableList.add(new Room(0, "Create"));
        roomListView.setItems(roomObservableList);
        roomListView.setCellFactory(param -> new RoomCell(messageSender, window, this));
        //vBoxForListView.getChildren().add(groups);
    }

    private void createOnlineUsersListView() {
        onlineUsersListView.setItems(onlineUsersObservableList);
        onlineUsersListView.setCellFactory(param -> new UserCell());
    }

    private void createMessageListView() {
        messageListView.setItems(messageObservableList);
        messageListView.setCellFactory(param -> new MessageCell());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing");
        messageField.setOnAction(event -> {
            logger.info("Messagefield event");
            sendOnEnterPress();
        });

        logger.info("creating list listviews");
        createRoomListView();
        createMessageListView();
        createOnlineUsersListView();

        nameofCurrentRoom.setText("General");
    }
}
