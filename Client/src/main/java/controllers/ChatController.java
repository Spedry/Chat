package controllers;

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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.io.IOException;
import java.util.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;
import javafx.scene.control.ListCell;


public class ChatController {

    public TextField messageField;
    public ListView chatMessageListView;
    public static String jsonObject = null;
    Client client = Client.getInstance();
    private List<String> messages     = new ArrayList<>();
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
        setListView();
    }

    public void setListView()
    {
        messages.add("String 1");
        messages.add("String 2");
        messages.add("String 3");
        messages.add("String 4");
        observableList.setAll(messages);
        chatMessageListView.setItems(observableList);
        chatMessageListView.setCellFactory(new Callback<ListView<String>, javafx.scene.control.ListCell<String>>()
        {
            @Override
            public ListCell<String> call(ListView<String> listView)
            {
                return new ListViewCell();
            }
        });
    }

    public class ListViewCell extends ListCell<String>
    {
        //@Override
        public void updateItem(String username, String message, boolean empty)
        {
            //super.updateItem(username, message, empty);
            if(message != null)
            {
                CellController data = new CellController();
                data.setInfo(username, message);
                setGraphic(data.getBox());
            }
        }
    }
}
