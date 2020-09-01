package sk.Spedry.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread thread = new Thread(sk.Spedry.client.Client.getInstance());
        thread.setDaemon(true);
        thread.start();

        window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/loginScene.fxml"));
        Scene login = new Scene(root);
        window.setScene(login);
        window.show();
    }

    public static void chatScene() throws IOException {
        Parent register = FXMLLoader.load(App.class.getResource("/chatScene.fxml"));
        Scene scene = new Scene(register);
        window.setScene(scene);
        window.show();
    }
}