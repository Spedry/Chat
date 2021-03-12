package client;

import hash.RSA.RSA;
import controllers.ChatController;
import controllers.LoginController;
import javafx.application.Platform;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Methods;
import shortcuts_for_M_and_V.Variables;
import java.io.*;
import java.net.Socket;
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
    private BufferedReader br;
    @Getter
    private LinkedBlockingQueue<JSONObject> dataQueue;
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
            logger.error(Variables.IOEXCEPTION, ioe);
        }
    }

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
            logger.error(Variables.IOEXCEPTION, ioe);
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
            logger.info(Variables.IOEXCEPTION, ioe);
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
                switch ((jsonObject = dataQueue.take()).getString(Variables.ID)) {
                    case Variables.LOGIN_OF_USER:
                        if (key) {
                            setPublicKey();
                            key = false;
                        } else {
                            logger.info("Case for " + Variables.LOGIN_OF_USER);
                            if (jsonObject.getJSONObject(Variables.DATA).getBoolean(Variables.ATTEMPT)) {
                                logger.info(Variables.LOGIN_OF_USER + " was successful");
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
                                logger.info(Variables.LOGIN_OF_USER + " was unsuccessful");
                            key = true;
                        }
                        break;
                    case Variables.REGISTRATION_OF_NEW_USER:
                        if (key) {
                            setPublicKey();
                            key = false;
                        } else {
                            logger.info("Case for " + Variables.REGISTRATION_OF_NEW_USER);
                            if (jsonObject.getJSONObject(Variables.DATA).getBoolean(Variables.ATTEMPT)) {
                                logger.info(Variables.REGISTRATION_OF_NEW_USER + " was successful");
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
                                logger.info(Variables.REGISTRATION_OF_NEW_USER + " was unsuccessful");
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
            while (!(jsonObject = dataQueue.take()).equals("ENDJSON")) {
                logger.info("Data taken from dataQueue: " + jsonObject);
                switch (jsonObject.getString(Variables.ID)) {
                    case Variables.MESSAGE_FROM_USER:
                        logger.info("Case for " + Variables.MESSAGE_FROM_USER);
                        final String userName = jsonObject.getJSONObject(Variables.DATA).getString(Variables.USERNAME);
                        final String message = jsonObject.getJSONObject(Variables.DATA).getString(Variables.MESSAGE);
                        Platform.runLater(() ->
                                chatController.showMessage(userName, message));
                        break;
                    case Variables.SHOW_LOGIN_OF_ONLINE_USER:
                        logger.info("Case for " + Variables.SHOW_LOGIN_OF_ONLINE_USER);
                        JSONArray jsonArray = jsonObject.getJSONArray(Variables.DATA);
                        List<String> listofOnlineUsers = new ArrayList<>();
                        for (int i=0; i < jsonArray.length(); i++) {
                            listofOnlineUsers.add(jsonArray.getString(i));
                        }
                        Platform.runLater(() ->
                                chatController.showOnlineUser(listofOnlineUsers));
                        break;
                    case Variables.SHOW_ONLINE_USER:
                        logger.info("Case for " + Variables.SHOW_ONLINE_USER);
                        Platform.runLater(() ->
                                chatController.addOnlineUser(jsonObject.getJSONObject(Variables.DATA).getString(Variables.USERNAME)));
                        break;
                    case Variables.DELETE_ONLINE_USER:
                        logger.info("Case for " + Variables.DELETE_ONLINE_USER);
                        Platform.runLater(() ->
                                chatController.deleteOnlineUser(jsonObject.getJSONObject(Variables.DATA).getString(Variables.USERNAME)));
                        break;
                    case Variables.CREATE_NEW_ROOM:
                        logger.info("Case for " + Variables.CREATE_NEW_ROOM);
                        Platform.runLater(() ->
                                chatController.addRoomButton(jsonObject.getJSONObject(Variables.DATA).getInt(Variables.ROOM_ID),
                                        jsonObject.getJSONObject(Variables.DATA).getString(Variables.ROOM_NAME)));
                        break;
                    case Variables.LOAD_INTO_THE_ROOM:
                        logger.info("Case for " + Variables.LOAD_INTO_THE_ROOM);
                        Platform.runLater(() -> {
                                chatController.addRoomButton(jsonObject.getJSONObject(Variables.DATA).getInt(Variables.ROOM_ID),
                                jsonObject.getJSONObject(Variables.DATA).getString(Variables.ROOM_NAME));
                                chatController.setNameofCurrentRoom(jsonObject.getJSONObject(Variables.DATA).getString(Variables.ROOM_NAME));
                            chatController.setCurrentRoomID(jsonObject.getJSONObject(Variables.DATA).getInt(Variables.ROOM_ID));

                        });
                        break;
                    case Variables.LOAD_AN_EXISTING_ROOM:
                        logger.info("Case for " + Variables.LOAD_AN_EXISTING_ROOM);
                        Platform.runLater(() -> {
                            chatController.setNameofCurrentRoom(jsonObject.getJSONObject(Variables.DATA).getString(Variables.ROOM_NAME));
                            chatController.clearMessageListView();
                        });
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
        getPrintWriter().println(Methods.createJson(Variables.RIVEST_SHAMIR_ADLEMAN, Variables.KEY, rsa.getBase64EncodedPublicKey(), null, null));
        if(rsa.isOthersSidePublicKeyNull())
            rsa.setOthersSidePublicKey((jsonObject = dataQueue.take()).getJSONObject(Variables.DATA).getString(Variables.KEY));
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    private void setPublicKey() { // OMRKNÚŤ
        switch (jsonObject.getString(Variables.ID)) {
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
}
