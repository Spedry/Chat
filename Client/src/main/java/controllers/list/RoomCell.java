package controllers.list;

import client.Application;
import client.MessageSender;
import controllers.PopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RoomCell extends ListCell<Room> {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final Button openRoom = new Button("OPEN");
    private final Button createRoom = new Button("CREATE");
    private final Stage window;
    private final MessageSender messageSender;
    public RoomCell(MessageSender messageSender, Stage window) {
        super();
        this.messageSender = messageSender;
        this.window = window;
        //button.setOnAction(event -> getListView().getItems().remove(getItem())); mazanie do budúcna to bude užitočné
        openRoom.setOnAction(e -> {
            logger.info("I'll load an existing room");
            loadAnExistingRoom();
        });
        createRoom.setOnAction(e -> {
            logger.info("I'll create a new room");

            AnchorPane anchorPane;
            Popup popup = new Popup();
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("/fxml/popup.fxml"));



            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(window);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("This is a Dialog"));
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });
    }

    private void loadAnExistingRoom() {
        messageSender.printWriter(createJson("LR", "RoomID", getItem().getID(), null, null));
    }

    @Override
    protected void updateItem(Room room, boolean empty) {
        super.updateItem(room, empty);
        setGraphic(null);

        if (room != null && !empty && room.getID() != 000) {
            openRoom.setText(room.getName());
            setGraphic(openRoom);
        } else if (room != null && !empty && room.getID() == 000) {
            openRoom.setText(room.getName());
            setGraphic(createRoom);
        }
    }

    private JSONObject createJson(@NonNull String IDString,
                                  @NonNull String dataOne,
                                  @NonNull Object objectOne,
                                  String dataTwo,
                                  Object objectTwo) throws JSONException {
        String data = "Data";
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if (dataTwo != null && objectTwo != null)
            dataOfJsonObject.put(dataTwo, objectTwo);
        return new JSONObject()
                .put("ID", IDString)
                .put(data, dataOfJsonObject);
    }
}