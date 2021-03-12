package controllers.list;

import client.Application;
import client.MessageSender;
import controllers.ChatController;
import controllers.PopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shortcuts_for_M_and_V.Methods;
import shortcuts_for_M_and_V.Variables;

import java.io.IOException;

public class RoomCell extends ListCell<Room> {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final Button openRoom = new Button("OPEN");
    private final Button createRoom = new Button("CREATE");
    private final ChatController chatController;
    private final MessageSender messageSender;

    public RoomCell(MessageSender messageSender, Stage window, ChatController chatController) {
        super();
        this.messageSender = messageSender;
        this.chatController = chatController;
        //button.setOnAction(event -> getListView().getItems().remove(getItem())); mazanie do budúcna to bude užitočné
        openRoom.setOnAction(e -> {
            logger.info("I'll load an existing room");
            loadAnExistingRoom();
            chatController.clearMessageListView();
        });
        createRoom.setOnAction(e -> {
            logger.info("I'll create a new room");
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(window);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("This is Popup"));
            Scene newScene = new Scene(dialogVbox, 300, 200);
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/popup.fxml"));
            PopupController popupController = new PopupController(popup, messageSender);

            loader.setController(popupController);
            try {
                newScene = new Scene(loader.load());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            popup.setScene(newScene);
            popup.show();
        });
    }


    private void loadAnExistingRoom() {
        logger.info("Sending a request to load an existing room");
        messageSender.printWriter(Methods.createJson(Variables.LOAD_AN_EXISTING_ROOM, Variables.ROOM_ID, getItem().getID()));
        logger.info("Setting current room ID to: " + getItem().getID());
        chatController.setCurrentRoomID(getItem().getID());
        logger.info("Current room ID was set");
        chatController.messageField.clear();
    }

    @Override
    protected void updateItem(Room room, boolean empty) {
        super.updateItem(room, empty);
        setGraphic(null);

        if (room != null && !empty && room.getID() != 0) {
            openRoom.setText(room.getName());
            setGraphic(openRoom);
        } else if (room != null && !empty && room.getID() == 0) {
            openRoom.setText(room.getName());
            setGraphic(createRoom);
        }
    }
}