package sk.Spedry.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage window;
    //ako spraviť chat hromadný alebo jednodný alebo kombo
    //identifikovať prichadzajucu spravu na server login, register, messege
        //odoslaním informačnej spravy informujúcej o typu dát ktorú prýdu ešte pred odoslaním dát
        //pribaliť k dátam informačnu spravu
        //Port???

        //Protokol medzi klienta-server
    //odosielanie obrazkov, emoji, gif
    //použitá literatúra knihy a hlavne stranky ako stackoverflow
    //čo presne budem popisovať v mojej bakalarke napr jednotlive kapitoli, ich počet a na čo budú zamerané
    //Odosielať JSON object alebo JAVA object na server
    //je zložite spraviť .exe súbor
    //čas odoslania správy a "zobrazené" z FB
    //použiť oracle DOCS knižnice
    //JAVA knihy

    //použiť JSON
    //identifikácia pomocou IF alebo SWITCH CASE
    //apache tomcat log4J knižnica
    //vytvoriť script na odosielanie správ z cca 20000 users
    // testovanie pomocou pythom alebo postman na odosielanie správ

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*socket = Client.createSocket();
        socket.setKeepAlive(true);
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*java.awt.EventQueue.invokeLater(() -> {
            socket = Client.createSocket();

            try {
                socket.setKeepAlive(true);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });*/
        Thread thread = new Thread(Client.getInstance());
        thread.start();

        window = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        Scene login = new Scene(root);
        window.setScene(login);
        window.show();
    }
}