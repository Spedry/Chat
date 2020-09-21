package sk.Spedry.client;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {

    private static volatile Client instance;

    private Client() {
        String hostname = null;
        try {
            InetAddress ip;
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
        if(instance == null){ //if there is no instance available... create new one
            synchronized (Client.class) {
                if(instance == null){ // double check - Thread Safe Singleton: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
                    instance = new Client();
                }
            }
        }
        return instance;
    }

    static final int PORT = 50000;
    private Socket socket;
    private PrintWriter out;
    private InputStreamReader in;
    private BufferedReader inputReader;
    private String input;
    private static JSONObject jsonObject;
    public static Stage window;
    private static LinkedBlockingQueue<JSONObject> dataQueue;

    private void getJsonObject(BufferedReader br) throws IOException, JSONException, InterruptedException {
        String data;

        while((data = br.readLine()) != null) {
            dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            System.out.println(data);
        }
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new InputStreamReader(socket.getInputStream());
            dataQueue = new LinkedBlockingQueue<>();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(in)) {
            Thread thread = new Thread(() -> {
                try {

                    getJsonObject(br);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.setDaemon(true);
            thread.start();
            boolean login = true;
            while (login) {
                /*String data;
                while ((data = br.readLine()) != null) {
                    // možno nepotrebný while cyklus keďže  odpoveď by mala dôjsť vždy
                    // môže nastať error v prípade ak načátanie dát bude rýchlejšie
                    // ako spracovanie a odoslanie odpovedi servera??
                    System.out.println("while data: " + data);
                    jsonObject = new JSONObject(data);
                    break;
                }*/
                switch ((jsonObject = dataQueue.take()).getString("ID")) {
                    case "LoU":
                        System.out.println("lou funguje");
                        if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                            System.out.println("bolo prijate true");
                            login = false;
                            Platform.runLater(() -> {
                                try {
                                    App.chatScene();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            });
                        } else
                            System.out.println("bolo prijate false");
                        break;
                    case "RoNU":
                        System.out.println("ronu funguje");
                        if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                            Timer timer = new Timer();
                            timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        Platform.runLater(() -> {
                                            try {
                                                App.registrationsSuccessful();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                        timer.cancel();
                                    }
                                }, 5000
                            );
                        }
                        else
                            System.out.println("meno je duplicitné");
                        break;
                    default:
                        System.out.println("neznáme ID");
                        break;
                }
            }
            System.out.println("lets GOOOOO");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("ioe: " + socket.isClosed());
            System.out.println(socket.isConnected());
        } /*catch (InterruptedException ie) {
            ie.printStackTrace();
            System.out.println("ie: " + socket.isClosed());
        }*/ catch (JSONException jsone) {
            jsone.printStackTrace();
            System.out.println("json: " + socket.isClosed());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    public void setInput(String input) {
        getPrintWriter().println(input);
    }
}
