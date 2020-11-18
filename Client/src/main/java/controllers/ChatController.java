package controllers;

import client.App;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.scene.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;


public class ChatController implements Initializable {


    //Client client = Client.getInstance();
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String   data = "Data", userName = "Username", hash = "Password", message = "Message",
            messagefromUser = "MfU", showLoginofUser = "SLoU";
    private String jsonObject = null;
    LinkedBlockingQueue<JSONObject> dataQueue;
    @FXML
    public ListView peopleOnline;
    @FXML
    public TextField messageField;
    @FXML
    public ScrollPane chatMessageScrollPane;
    @FXML
    public VBox chatBox;

    public ChatController(LinkedBlockingQueue<JSONObject> dataQueue) {
        this.dataQueue = dataQueue;
    }

    @FXML
    public void sendOnEnterPress() throws JSONException {
        jsonObject = new JSONObject()
                .put("ID", messagefromUser) //message from User
                .put(data, new JSONObject()
                        .put(message, messageField.getText()))
                .toString();
        App.getInstance().getClient().setInput(jsonObject); //PREROBIŤ
        messageField.clear();
    }

    /*public void showMessage(String string) {
        VBox root = new VBox();
        root.getChildren().addAll(new Label(string), new Label(string), new Label(string), new Label(string));
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        chatMessageScrollPane.setContent(root);
        chatMessageScrollPane.setPannable(true);
    }*/

    public void showMessage(String userName, String userMessage) {
        HBoxController hBoxController = null;
        VBox hBox = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HBox.fxml"));
            hBox = loader.load();
            hBoxController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*hBoxController.setUserNameHBox(new Label(userName));
        hBoxController.setUserMessageHBox(new Label(message));
        hBoxController.setImgHBox(new ImageView(new Image("/icons/user.png")));*/

        /*try {
            hBox = FXMLLoader.load(App.class.getResource("/fxml/HBox.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //HBoxController hBoxController = new HBoxController(new Label(userName), new Label(message), new ImageView(new Image("/icons/user.png")));

        hBoxController.setContent(userName, userMessage, null); //Pridat img
        /*Label msgText = new Label(message);
        Label usrnText = new Label(userName + ": ");
        msgText.setTextFill(Color.web("#FFFFFF"));
        usrnText.setTextFill(Color.web("#FFFFFF"));
        TextFlow tempFlow=new TextFlow();
        tempFlow.getChildren().addAll(usrnText, msgText);
        tempFlow.setMaxWidth(200);

        TextFlow flow=new TextFlow(tempFlow);
        HBox hbox=new HBox(12);
        //HBoxController hbox = new HBoxController(new Label(userName), new Label(message), new Image("C:\\Users\\Spedry\\Desktop\\EkM6oFRU0AEUHJs.jpg"));
        //chatBox.setAlignment(Pos.TOP_LEFT);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(flow);
        //Platform.runLater(() -> chatBox.getChildren().add(hbox));*/
        chatBox.getChildren().add(hBox);

        logger.info(userName + " " + userMessage);
    }

    public void showOnlineUser(List<String> listofOnlineUsers) {
        peopleOnline.getItems().clear();
        for (String user : listofOnlineUsers) {
            /*Text userText = new Text(user);
            TextFlow tempFlow=new TextFlow();
            tempFlow.getChildren().add(userText);
            //tempFlow.setMaxWidth(200);*/
            LabelController labelController = null;
            Label label = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Label.fxml"));
                label = loader.load();
                labelController = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
            labelController.setContent(user);
            //TextFlow flow=new TextFlow(tempFlow);
            HBox hBox = new HBox();
            hBox.setMaxWidth(peopleOnline.getWidth() - 20);
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().add(label);
            peopleOnline.getItems().add(hBox);
        }
    }



    /*public void test(String finalUserName, String finalMessage) {
        Platform.runLater(() -> showMessage(finalUserName, finalMessage));
    }*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initializing");
        messageField.setOnAction(event -> {
            logger.info("Messagefield event");
            sendOnEnterPress();
        });

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    JSONObject jsonObject = null;
                    switch ((jsonObject = dataQueue.take()).getString("ID")) { //PREROBIŤ
                        case messagefromUser:
                            String userName = null, message = null;
                            userName = jsonObject.getJSONObject(data).getString(this.userName);
                            message = jsonObject.getJSONObject(data).getString(this.message);
                            logger.info("Data taken from dataQueue: " + jsonObject);
                            String finalUserName = userName;
                            String finalMessage = message;
                            Platform.runLater(() -> showMessage(finalUserName, finalMessage));
                            break;
                        case showLoginofUser:
                            JSONArray jsonArray = jsonObject.getJSONArray(data);
                            List<String> listofOnlineUsers = new ArrayList<>();
                            for (int i=0; i < jsonArray.length(); i++) {
                                listofOnlineUsers.add(jsonArray.getString(i));
                            }
                            Platform.runLater(() -> showOnlineUser(listofOnlineUsers));
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /*public void test2(String userName, String message) {
        Text msgText = new Text(message);
        Text usrnText = new Text(userName);
        TextFlow tempFlow=new TextFlow();
        tempFlow.getChildren().addAll(usrnText, msgText);
        tempFlow.setMaxWidth(200);

        TextFlow flow=new TextFlow(tempFlow);
        HBox x = new HBox();
        x.setMaxWidth(chatPane.getWidth() - 20);
        x.setAlignment(Pos.CENTER_LEFT);
        x.getChildren().add(flow);
        chatPane.getItems().add(x);
        logger.info("test2 metoda");
        /*Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                Text msgText = new Text(message);
                Text usrnText = new Text(userName);
                TextFlow tempFlow=new TextFlow();
                tempFlow.getChildren().addAll(usrnText, msgText);
                tempFlow.setMaxWidth(200);

                TextFlow flow=new TextFlow(tempFlow);
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.CENTER_LEFT);
                x.getChildren().add(flow);
                logger.info(userName + "   " + message);

                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
    }*/

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageField.vvalueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
    }*/

    /*public boolean update(String username,String message) throws RemoteException {
        Text text=new Text(message);

        text.setFill(Color.WHITE);
        text.getStyleClass().add("message");
        TextFlow tempFlow=new TextFlow();
        if(!this.username.equals(username)){
            Text txtName=new Text(username + "\n");
            txtName.getStyleClass().add("txtName");
            tempFlow.getChildren().add(txtName);
        }

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(200);

        TextFlow flow=new TextFlow(tempFlow);

        HBox hbox=new HBox(12);

        Circle img =new Circle(32,32,16);
        try{
            System.out.println(username);
            String path= new File(String.format("resources/user-images/%s.png", username)).toURI().toString();
            img.setFill(new ImagePattern(new Image(path)));
        }catch (Exception ex){
            String path= new File("resources/user-images/user.png").toURI().toString();
            img.setFill(new ImagePattern(new Image(path)));
        }

        img.getStyleClass().add("imageView");
        if(!this.username.equals(username)){

            tempFlow.getStyleClass().add("tempFlowFlipped");
            flow.getStyleClass().add("textFlowFlipped");
            chatBox.setAlignment(Pos.TOP_LEFT);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().add(img);
            hbox.getChildren().add(flow);

        }else{
            text.setFill(Color.WHITE);
            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(flow);
            hbox.getChildren().add(img);
        }

        hbox.getStyleClass().add("hbox");
        Platform.runLater(() -> chatBox.getChildren().addAll(hbox));

        return true;

    }*/
    
    /*public void setListView()
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
    }*/
}