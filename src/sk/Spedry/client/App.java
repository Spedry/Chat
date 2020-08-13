package sk.Spedry.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread thread = new Thread(sk.Spedry.client.Client.getInstance());
        thread.start();

        window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Scene login = new Scene(root);
        window.setScene(login);
        window.show();
    }
}