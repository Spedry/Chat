package sk.Spedry.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginFX {
    private static Button loginButton;
    private static TextField name, password;
    private static Label wellcome, meno, heslo, chyba;
    private static VBox vBox;

    public static Scene Scene(Stage stage, Scene scene) {
        Scene loginWin = null;

        loginButton = new Button("Login");
        name = new TextField("Meno");
        password = new TextField("Heslo");
        wellcome = new Label("Zadajte Vaše meno a heslo.");
        meno = new Label("Meno:");
        heslo = new Label("Heslo:");

        // definovať metodu
        loginButton.setOnAction(e -> {
            if (name.getText().equals("Spedry"/*funkcia na zistenie mena*/) && password.getText().equals("123"/*funkcia na zistenie hesla*/)) {
                System.out.println(name.getText() + " " + password.getText());
                stage.setScene(scene);
            }
        });

        vBox = new VBox();
        vBox.getChildren().addAll(wellcome, meno, name, heslo, password, loginButton);
        loginWin = new Scene(vBox, ChatFX.sirka, ChatFX.vyska);
        return loginWin;
    }
}
