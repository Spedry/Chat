package sk.Spedry.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sk.Spedry.Client.Client;

import java.net.Socket;

public class ChatFX extends Application {
    private static Socket socket;

    public static int sirka = 600, vyska = 350;

    Stage window; // celé okno
    Scene chatWin; // vnútro okna
    Label chatLabel;
    StackPane chatLayout;
    public static void main(String[] args) {
        socket = Client.createSocket();
        // štartuje app
        launch(args);
    }

    // telo app
    @Override
    public void start(Stage primaryStage) {
        // definovanie widnow ako primStage
        window = primaryStage;

        chatLabel = new Label("CHAT!!!");

        chatLayout = new StackPane();
        chatLayout.getChildren().add(chatLabel);
        chatWin = new Scene(chatLayout, sirka, vyska);


        window.setTitle("Panchatune");
        // definowať ktorá scéna sa ma zobraziť vo window
        window.setScene(LoginFX.Scene(window, chatWin));
        // ukáž window
        window.show();
    }
}
