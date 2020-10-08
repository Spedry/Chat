package controllers;

import client.App;
import client.Client;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;


public class ChatController implements Initializable {


    //Client client = Client.getInstance();
    private final Logger logger = LogManager.getLogger(this.getClass());
    public Label label;
    private String jsonObject = null;
    @FXML
    public TextField messageField;
    @FXML
    public ScrollPane chatMessageScrollPane;
    @FXML
    public VBox chatBox;
    @FXML
    public ListView<HBox> chatPane;
    /*public void ChatController(ActionEvent actionEvent) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("/chatScene.fxml"));
        Scene scene = new Scene(register);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }*/

    @FXML
    public void sendOnEnterPress(javafx.event.ActionEvent actionEvent) throws JSONException {
        jsonObject = new JSONObject()
                .put("ID", "MfU") //message from User
                .put("Data", new JSONObject()
                        .put("Message", messageField.getText()))
                .toString();
        App.getInstance().getClient().setInput(jsonObject);
        //client.setInput(jsonObject);
        messageField.clear();
        //test("MENO_TEST: ", "SPRAVA_TEST");
    }

    public void showMessage(String string) {
        VBox root = new VBox();
        root.getChildren().addAll(new Label(string), new Label(string), new Label(string), new Label(string));
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        chatMessageScrollPane.setContent(root);
        chatMessageScrollPane.setPannable(true);
    }

    public void test(String userName, String message)  {
        Label msgText = new Label(message);
        Label usrnText = new Label(userName);
        TextFlow tempFlow=new TextFlow();
        tempFlow.getChildren().addAll(usrnText, msgText);
        tempFlow.setMaxWidth(200);

        TextFlow flow=new TextFlow(tempFlow);
        HBox hbox=new HBox(12);

        //chatBox.setAlignment(Pos.TOP_LEFT);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(flow);
        //Platform.runLater(() -> chatBox.getChildren().add(hbox));
        chatBox.getChildren().add(hbox);

        logger.info(userName + " " + message);
        logger.info("test metoda");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

            new Thread(() -> {
                JSONObject J = null;
                while (true) {
                    try {
                        J = App.getInstance().getClient().dataQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("Data taken from dataQueue: " + jsonObject);
                    JSONObject finalJ = J;
                    Platform.runLater(() -> test("userName", finalJ.toString()));
                }
            }).start();
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