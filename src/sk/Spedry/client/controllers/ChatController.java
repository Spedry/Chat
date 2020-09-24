package sk.Spedry.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import sk.Spedry.client.Client;

import javax.security.auth.callback.Callback;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;

public class ChatController {

    public TextField messageField;
    public ListView chatMessageListView;
    public static String jsonObject = null;
    Client client = Client.getInstance();
    private Set<String> messages;
    ObservableList observableList = FXCollections.observableArrayList();
    private final Logger logger = LogManager.getLogger(this.getClass());

    /*public void ChatController(ActionEvent actionEvent) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/chatScene.fxml"));
        Scene scene = new Scene(register);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }*/

    public void sendOnEnterPress(javafx.event.ActionEvent actionEvent) throws JSONException {
        jsonObject = new JSONObject()
                .put("ID", "MfU") //message from User
                .put("Data", new JSONObject()
                        .put("Message", messageField.getText()))
                .toString();
        client.setInput(jsonObject);
        messageField.clear();
    }

    /*public void setListView(String message) {
        messages.add(message);
        observableList.setAll(messages);
        chatMessageListView.setItems(observableList);
        chatMessageListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListViewCell();
            }
        });
    }*/
}
