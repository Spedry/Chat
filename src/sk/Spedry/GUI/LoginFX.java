package sk.Spedry.GUI;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import data.Overenie;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginFX {
    private static Button loginButton;
    private static TextField name, password;
    private static Label wellcome, meno, heslo, chyba;
    private static VBox vBox;

    public static Scene Scene(Stage stage, Scene scene, Socket socket) {
        Scene loginWin = null;

        loginButton = new Button("Login");
        name = new TextField("Meno");
        password = new TextField("Heslo");
        wellcome = new Label("Zadajte Vaše meno a heslo.");
        meno = new Label("Meno:");
        heslo = new Label("Heslo:");


        // definovať metodu
        loginButton.setOnAction(e -> {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                //ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Overenie overenie = new Overenie(name.getText(), password.getText());
                objectOutputStream.writeObject(overenie);
                objectOutputStream.flush();
                objectOutputStream.close();
                if (name.getText().equals("Spedry"/*funkcia na zistenie mena*/) && password.getText().equals("123"/*funkcia na zistenie hesla*/)) {
                    stage.setScene(scene);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vBox = new VBox();
        vBox.getChildren().addAll(wellcome, meno, name, heslo, password, loginButton);
        loginWin = new Scene(vBox, ChatFX.sirka, ChatFX.vyska);
        return loginWin;
    }
}
