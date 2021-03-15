package client;

import controllers.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Stage window;
    private static final Logger logger = LogManager.getLogger(Application.class);
    LoginController loginController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("App is starting...");
        window = primaryStage;
        loginController = new LoginController(window);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginScene.fxml"));
        loader.setController(loginController);
        Parent root = loader.load();
        Scene login = new Scene(root);
        window.setScene(login);
        window.initStyle(StageStyle.UNDECORATED);
        logger.info("Set scene to loginScene");
        window.show();
        logger.info("Show window of app");
    }

    @Override
    public void stop() {
        try {
            loginController.getClientSide().closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}