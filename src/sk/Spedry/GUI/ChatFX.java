package sk.Spedry.GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatFX extends Application {

    public static int sirka = 600, vyska = 350;

    Stage window; // celé okno
    Scene chatWin; // vnútro okna
    Label chatLabel;
    StackPane chatLayout;
    public static void main(String[] args) {
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
