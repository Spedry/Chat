package client;

import controllers.ChatController;
import controllers.LoginController;
import controllers.RegisterController;
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
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.plaf.TreeUI;
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
    @Setter
    private LoginController loginController;
    private final int port = 50000;
    private final String hostname = "213.160.168.243";
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

    public Client(LoginController loginController) {
        this.loginController = loginController;
        /*try {
            InetAddress ip;
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException uhe) {
            logger.warn("IP address of a host could not be determined", uhe);
        }*/
        try {
            socket = new Socket(hostname, port);
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

    private void incomingDataHandler(BufferedReader br) throws IOException, JSONException, InterruptedException {
        String data;
        while((data = br.readLine()) != null) {
            logger.info("Data received from while cycle: " + data);
            dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
        }
        logger.info("While cycle to get messages ended");
        socket.close();
        logger.info("Was socket connected: " + socket.isConnected());
        logger.info("Was socket closed: " + socket.isClosed());
    }
    boolean key = true;
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
                }
            });
            logger.info("New thread to handle incoming data was created");
            //incomingDataHandlerThread.setDaemon(true);
            incomingDataHandlerThread.start();
            logger.info("Start of while cycle to login/register");

            while (true) {
                switch ((jsonObject = dataQueue.take()).getString("ID")) {
                    case "LoU":
                        if (key) {
                            setPublicKey();
                            key = false;
                        } else {
                            logger.info("Case for LoU");
                            if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                                logger.info("LoU was successful");
                                login = false;
                                Platform.runLater(() -> {
                                    try {
                                        ChatController chatController = new ChatController(this, dataQueue);
                                        loginController.chatScene(chatController);
                                    } catch (IOException ioe) {
                                        ioe.printStackTrace();
                                    }
                                });
                                logger.info("Scene was changed to chatScene");
                            } else
                                logger.info("LoU was unsuccessful");
                            key = true;
                        }
                        break;
                    case "RoNU":
                        if (key) {
                            setPublicKey();
                            key = false;
                        } else {
                            logger.info("Case for RoNU");
                            if (jsonObject.getJSONObject("Data").getBoolean("Attempt")) {
                                logger.info("RoNU was successful");
                                Timer timer = new Timer();
                                timer.schedule(
                                        new TimerTask() {
                                            @Override
                                            public void run() {
                                                Platform.runLater(() -> {
                                                    try {
                                                        loginController.backToLoginScene(loginController);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                                timer.cancel();
                                            }
                                        }, 3000
                                );
                                logger.info("Scene was changed to loginScene");
                            } else
                                logger.info("RuNU was unsuccessful");
                            key = true;
                        }
                        break;
                    default:
                        logger.warn("unknown ID");
                        break;
                }
            }
            //logger.info("End of while cycle to login/register");
            //logger.info("Start of while cycle which will manage incoming messages");

            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatScene.fxml"));
            loader.setController(chatController);*/

        }  catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .TAKE()", ie);
        }
    }

    private void setPublicKey() {

        switch (jsonObject.getString("ID")) {
            case "LoU":
                loginController.getPassHash().setSalt(jsonObject);
                loginController.loginUser();
                break;
            case "RoNU":
                loginController.getRegisterController().getPassHash().setSalt(jsonObject);
                loginController.getRegisterController().registerUser();
                break;
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
