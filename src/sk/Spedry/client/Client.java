package sk.Spedry.client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.stage.Stage;
import sk.Spedry.client.controllers.ChatController;

public class Client implements Runnable {
    static final int PORT = 50000;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader inputReader;
    private String input;
    private static volatile Client instance;
    private static JSONObject jsonObject;
    public static Stage window;

    private Client() {
        String hostname = null;
        try {
            InetAddress ip = null;
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new Socket(hostname, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Singleton pattern https://en.wikipedia.org/wiki/Singleton_pattern
    public static Client getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new Client();
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                if (input == null) {
                    Thread.sleep(1000);
                    System.out.println(input);
                } else {
                    inputReader = new BufferedReader(new StringReader(input));
                    while ((input = inputReader.readLine()) != null) {
                        getPrintWriter().println(input);
                    }
                    System.out.println(socket.isClosed());
                    System.out.println(socket.isConnected());
                    jsonObject = new JSONObject(in.readLine());
                    switch (jsonObject.getString("ID")) {
                        case "LoU":
                            System.out.println("lou funguje");
                            if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                                System.out.println("bolo prijate true");
                                Platform.runLater(() -> {
                                    try {
                                        App.chatScene();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                            else
                                System.out.println("bolo prijate false");
                            break;
                        case "RoNU":
                            System.out.println("ronu funguje");
                            if (jsonObject.getJSONObject("Data").getBoolean("Attempt"))
                                System.out.println("bol si zaregistrovaný");
                            else
                                System.out.println("meno je duplicitné");
                            break;
                        default:
                            System.out.println("neznáme ID");
                            //return;
                            break;
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("ioe: " + socket.isClosed());
            System.out.println(socket.isConnected());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.out.println("ie: " + socket.isClosed());
        } catch (JSONException jsone) {
            jsone.printStackTrace();
            System.out.println("json: " + socket.isClosed());
        }
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
