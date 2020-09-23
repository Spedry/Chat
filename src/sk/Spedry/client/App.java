package sk.Spedry.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage window;

    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("App is starting...");
        Thread clientSideHandlerThread = new Thread(Client.getInstance());
        clientSideHandlerThread.setDaemon(true);
        clientSideHandlerThread.start();
        logger.info("Thread for communication with server was created");

        window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/loginScene.fxml"));
        Scene login = new Scene(root);
        window.setScene(login);
        logger.info("Set scene to loginScene");
        window.show();
        logger.info("Show window of app");
    }

    public static void chatScene() throws IOException {
        Parent chatScene = FXMLLoader.load(App.class.getResource("/chatScene.fxml"));
        Scene scene = new Scene(chatScene);
        window.setScene(scene);
        logger.info("Set scene to chatScene");
        window.show();
        logger.info("Show window of app");
    }
    public static void registrationsSuccessful() throws IOException {
        Parent backToLoginScene = FXMLLoader.load(App.class.getResource("/loginScene.fxml"));
        Scene scene = new Scene(backToLoginScene);
        window.setScene(scene);
        logger.info("Set scene to loginScene");
        window.show();
        logger.info("Show window of app");
    }
}