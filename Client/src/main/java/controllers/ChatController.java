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
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatController implements Initializable {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String data = "Data", userName = "Username", hash = "Password", message = "Message",
            messagefromUser = "MfU", showLoginofUser = "SLoU";
    private final Stage window;
    private final MessageSender messageSender;
    private LinkedBlockingQueue<JSONObject> dataQueue;
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
        JSONObject jsonObject = new JSONObject()
                .put("ID", messagefromUser) //message from User
                .put(data, new JSONObject()
                        .put(message, messageField.getText()));
        messageSender.printWriter(jsonObject);
        messageField.clear();
    }

    @FXML
    public void goToHomeOnAction() {
        logger.info("goToHome button");
        messageSender.printWriter(createJson("LR", "RoomID", 000,null, null));
    }

    @FXML
    public void switchBetweenGroupAndFriendOnAction() {
        logger.info("switchBetweenGroupAndFriend button");
        // TODO: switch scény medzi skupinam a priateľmi
        roomObservableList.add(new Room("Group", 001));
        roomObservableList.add(new Room("Group", 002));
        roomObservableList.add(new Room("Group", 003));

    }

    public void showMessage(String userName, String userMessage) {
        messageObservableList.add(new Message(userName, userMessage));
        messageListView.scrollTo(messageListView.getItems().size() - 1);
    }

    public void showOnlineUser(List<String> listofOnlineUsers) {
        for (String onlineUser : listofOnlineUsers) {
            onlineUsersObservableList.add(onlineUser);
        }
    }

    public void addOnlineUser(String onlineUser) {
        onlineUsersObservableList.add(onlineUser);
    }

    public void deleteOnlineUser(String onlineUser) {
        for (String user: onlineUsersObservableList) {
            if (user.equals(onlineUser)) {
                logger.info("User: " + user + " was removed");
                onlineUsersObservableList.remove(user);
                break;
            }
        }
    }

    private void createRoomListView() {
        roomObservableList.add(new Room("Create", 000));
        roomListView.setItems(roomObservableList);
        roomListView.setCellFactory(param -> new RoomCell(messageSender, window));
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
    }
    private JSONObject createJson(@NonNull String IDString,
                                  @NonNull String dataOne,
                                  @NonNull Object objectOne,
                                  String dataTwo,
                                  Object objectTwo) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if (dataTwo != null && objectTwo != null)
            dataOfJsonObject.put(dataTwo, objectTwo);
        return new JSONObject()
                .put("ID", IDString)
                .put(data, dataOfJsonObject);
    }
}