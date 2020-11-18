package client;

import controllers.ChatController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {

    private static volatile Client instance;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private App app;
    public Client(App app) {
        String hostname = null;
        this.app = app;
        try {
            InetAddress ip;
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException uhe) {
            logger.warn("IP address of a host could not be determined", uhe);
        }
        try {
            socket = new Socket(hostname, PORT);
        } catch (IOException ioe) {
            logger.error(ioexception, ioe);
        }
    }
    // Singleton pattern https://en.wikipedia.org/wiki/Singleton_pattern
    /*public static Client getInstance() {
        if(instance == null){ //if there is no instance available... create new one
            synchronized (Client.class) {
                if(instance == null){ // double check - Thread Safe Singleton: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
                    instance = new Client();
                }
            }
        }
        return instance;
    }*/

    static final int PORT = 50000;
    private Socket socket;
    private PrintWriter out;
    private InputStreamReader in;
    private JSONObject jsonObject;
    BufferedReader br;
    @Getter
    public LinkedBlockingQueue<JSONObject> dataQueue;
    private final String   data = "Data", userName = "Username", hash = "Password", message = "Message",
            messagefromUser = "MfU", showLoginofUser = "SLoU";
    private final String ioexception = "Reading a network file and got disconnected.\n" +
            "Reading a local file that was no longer available.\n" +
            "Using some stream to read data and some other process closed the stream.\n" +
            "Trying to read/write a file, but don't have permission.\n" +
            "Trying to write to a file, but disk space was no longer available.\n" +
            "There are many more examples, but these are the most common, in my experience.";

    private boolean login = true;

    private void incomingDataHandler(BufferedReader br) throws IOException, JSONException, InterruptedException {
        String data;
        while((data = br.readLine()) != null) {
            logger.info("Data received from while cycle: " + data);
            dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            System.out.println(data);
        }
        logger.info("While cycle to get messages ended");
        socket.close();
        logger.info("Was socket connected: " + socket.isConnected());
        logger.info("Was socket closed: " + socket.isClosed());
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new InputStreamReader(socket.getInputStream());
            dataQueue = new LinkedBlockingQueue<>();
            br = new BufferedReader(in);
        } catch (IOException ioe) {
            logger.info(ioexception, ioe);
        }
        try {
            Thread incomingDataHandlerThread = new Thread(() -> {
                try {
                    incomingDataHandler(br);
                } catch (IOException ioe) {
                    logger.info(ioexception, ioe);
                } catch (JSONException jsone) {
                    logger.error("Error with JSONObject", jsone);
                } catch (InterruptedException ie) {
                    logger.error("Waiting thread was interrupted - .PUT()", ie);
                } /*finally {
                    try {
                        out.close();
                        in.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
            });
            logger.info("New thread to handle incoming data was created");
            //incomingDataHandlerThread.setDaemon(true);
            incomingDataHandlerThread.start();
            logger.info("Start of while cycle to login/register");
            while (login) {
                switch ((jsonObject = dataQueue.take()).getString("ID")) {
                    case "LoU":
                        logger.info("Case for LoU");
                        if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                            logger.info("LoU was successful");
                            login = false;
                            Platform.runLater(() -> {
                                try {
                                    ChatController chatController = new ChatController(dataQueue);
                                    app.chatScene(chatController);
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            });
                            logger.info("Scene was changed to chatScene");
                        } else
                            logger.info("LoU was unsuccessful");
                        break;
                    case "RoNU":
                        logger.info("RoNU was successful");
                        if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                            Timer timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            Platform.runLater(() -> {
                                                try {
                                                    app.registrationsSuccessful();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                            timer.cancel();
                                        }
                                    }, 5000
                            );
                            logger.info("Scene was changed to loginScene");
                        } else
                            logger.info("RuNU was unsuccessful");
                        break;
                    default:
                        logger.warn("unknown ID");
                        break;
                }
            }
            logger.info("End of while cycle to login/register");
            logger.info("Start of while cycle which will manage incoming messages");


            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatScene.fxml"));
            loader.setController(chatController);*/


        }  catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .TAKE()", ie);
        }
    }

    public void closeSocket() throws IOException {
        socket.shutdownOutput();
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    public void setInput(String input) {
        getPrintWriter().println(input);
    }
}
