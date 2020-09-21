package sk.Spedry.client.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import sk.Spedry.client.Client;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class ChatController {

    public TextField messageField;
    public ListView chatMessageListView;
    public static String jsonObject = null;
    Client client = Client.getInstance();

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
}
