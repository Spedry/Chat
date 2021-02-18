package client;

import Hashing.RSA.RSA;
import controllers.ChatController;
import controllers.LoginController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSide implements Runnable {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private LoginController loginController;
    @Setter
    private ChatController chatController;
    @Getter
    private MessageSender messageSender;
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
            messagefromUser = "MfU", showLoginofOnlineUser = "SLoOU";
    private final String ioexception = "Reading a network file and got disconnected.\n" +
            "Reading a local file that was no longer available.\n" +
            "Using some stream to read data and some other process closed the stream.\n" +
            "Trying to read/write a file, but don't have permission.\n" +
            "Trying to write to a file, but disk space was no longer available.\n" +
            "There are many more examples, but these are the most common, in my experience.";
    private boolean login = true;
    @Getter
    private RSA rsa;

    public ClientSide(LoginController loginController) {
        this.loginController = loginController;
        messageSender = new MessageSender(this);
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

    private void incomingDataHandler(@NonNull BufferedReader br) {
        try {
            String data;
            if ((data = br.readLine()) != null) {
                logger.debug("Data for RSA: " + data);
                dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            }

            logger.info("Start of while cycle to get JSONObject");
            while((data = br.readLine()) != null) {
                logger.info("Data was received"); //Data are in hash format
                dataQueue.put(new JSONObject(rsa.decrypt(data))); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            }
            logger.info("While cycle to get messages ended");

            socket.close();
            logger.info("Was socket connected: " + socket.isConnected());
            logger.info("Was socket closed: " + socket.isClosed());
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .PUT()", ie);
        } catch (IOException ioe) {
            logger.error(ioexception, ioe);
        }
    }
    boolean key = true;
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            dataQueue = new LinkedBlockingQueue<>();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ioe) {
            logger.info(ioexception, ioe);
        }
        try {
            logger.info("Creating thread to handle incoming data");
            Thread incomingDataHandlerThread = new Thread(() -> incomingDataHandler(br));
            incomingDataHandlerThread.start();
            logger.info("New thread to handle incoming data was created");

            logger.info("Receiving RSA public key");
            setRSAKey();
            logger.info("Key was received and successfully set");

            logger.info("Start of while cycle to login/register");
            while (login) {
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
                                        loginController.chatScene();
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
            logger.info("End of while cycle to login/register");

            logger.info("Start of while cycle which will manage incoming messages");
            while (true) {
                switch ((jsonObject = dataQueue.take()).getString("ID")) { //PREROBIŤ
                    case messagefromUser:
                        String userName = null, message = null;
                        userName = jsonObject.getJSONObject(data).getString(this.userName);
                        message = jsonObject.getJSONObject(data).getString(this.message);
                        logger.info("Data taken from dataQueue: " + jsonObject);
                        String finalUserName = userName;
                        String finalMessage = message;
                        Platform.runLater(() -> chatController.showMessage(finalUserName, finalMessage));
                        break;
                    case showLoginofOnlineUser:
                        JSONArray jsonArray = jsonObject.getJSONArray(data);
                        List<String> listofOnlineUsers = new ArrayList<>();
                        for (int i=0; i < jsonArray.length(); i++) {
                            listofOnlineUsers.add(jsonArray.getString(i));
                        }
                        Platform.runLater(() -> chatController.showOnlineUser(listofOnlineUsers));
                        break;
                    case "SOU":
                        Platform.runLater(() -> chatController.addOnlineUser(jsonObject.getJSONObject("Data").getString("Username")));
                        break;
                    case "DOU":
                        Platform.runLater(() -> chatController.deleteOnlineUser(jsonObject.getJSONObject("Data").getString("Username")));
                        break;
                }
            }

        }  catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .TAKE()", ie);
        } catch (NoSuchAlgorithmException nsae) {
            logger.error("Cryptographic algorithm is requested but is not available in the environment", nsae);
        }
    }

    private void setRSAKey() throws InterruptedException, NoSuchAlgorithmException {
        rsa = new RSA();
        getPrintWriter().println(createJson("RSA", "Key", rsa.getBase64EncodedPublicKey(), null, null));
        if(rsa.isOthersSidePublicKeyNull())
            rsa.setOthersSidePublicKey((jsonObject = dataQueue.take()).getJSONObject("Data").getString("Key"));
        //logger.info("KEY: " + rsa.test());

    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    private void setPublicKey() {

        switch (jsonObject.getString("ID")) {
            case "LoU":
                loginController.getHashing().setSalt(jsonObject);
                loginController.loginUser();
                break;
            case "RoNU":
                loginController.getRegisterController().getHashing().setSalt(jsonObject);
                loginController.getRegisterController().registerUser();
                break;
        }
    }

    public void closeSocket() throws IOException {
        socket.shutdownOutput();
    }



    private JSONObject createJson(@NonNull String IDString,
                                  @NonNull String dataOne,
                                  @NonNull Object objectOne,
                                  String dataTwo,
                                  Object objectTwo) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if (dataTwo != null && objectTwo != null)
            dataOfJsonObject.put(dataTwo, objectTwo);
        return new JSONObject()
                .put("ID", IDString)
                .put(data, dataOfJsonObject);
    }
}
