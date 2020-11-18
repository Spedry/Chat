package client;

import controllers.ChatController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.io.IOException;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Stage window;
    private Client client;
    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("App is starting...");
        window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/loginScene.fxml"));
        Scene login = new Scene(root);
        window.setScene(login);
        logger.info("Set scene to loginScene");
        window.show();
        logger.info("Show window of app");

        Thread clientSideHandlerThread = new Thread(client = new Client(this));
        clientSideHandlerThread.setDaemon(true);
        clientSideHandlerThread.start();
        logger.info("Thread for communication with server was created");
    }

    @Override
    public void stop() {
        try {
            client.closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void chatScene(ChatController chatController) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/chatScene.fxml"));
        loader.setController(chatController);
        Parent chatScene = loader.load();
        Scene scene = new Scene(chatScene);
        window.setScene(scene);
        logger.info("Set scene to chatScene");
        window.show();
        logger.info("Show window of app");
    }

    public static void registrationsSuccessful() throws IOException {
        Parent backToLoginScene = FXMLLoader.load(App.class.getResource("/fxml/loginScene.fxml"));
        Scene scene = new Scene(backToLoginScene);
        window.setScene(scene);
        logger.info("Set scene to loginScene");
        window.show();
        logger.info("Show window of app");
    }

    public void test(String userName, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatScene.fxml"));
        loader.load();
        ChatController chatController = loader.getController();
        chatController.showMessage(userName, message);
    }

    public ChatController test3() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatScene.fxml"));
        loader.load();
        return loader.getController();
    }

    public void test2() {
        new Thread(() -> {
            JSONObject jsonObject = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatScene.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatController chatController = loader.getController();

                try {
                    jsonObject = client.getDataQueue().take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //logger.info("Data taken from dataQueue: " + jsonObject);
                JSONObject finalJsonObject = jsonObject;
                Platform.runLater(() -> {
                    String s = finalJsonObject.toString();
                    chatController.showMessage("userName", "message");
                    logger.info("test sprava: " + s);
                });

        }).start();
    }

    private static App instance;
    public App() {
        instance = this;
    }
    public static App getInstance() {
        return instance;
    }
    public Client getClient() {
        return client;
    }
}