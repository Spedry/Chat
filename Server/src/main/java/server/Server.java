package server;

import database.GetPublicKey;
import database.LoginUser;
import database.RegisterUser;
import hash.Hashing;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;

public class Server implements Runnable {
    private final Socket prepojenie;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final MessageHandler messageHandler;
    public Server(Socket prepojenie, MessageHandler messageHandler) {
        this.prepojenie = prepojenie;
        this.messageHandler = messageHandler;
    }

    private PrintWriter out;
    private InputStreamReader in;
    private JSONObject jsonObject = null, ENDJSON;
    private LinkedBlockingQueue<JSONObject> dataQueue;
    private final String   data = "Data", userName = "Username", hash = "Password", message = "Message",
            messagefromUser = "MfU", loginofUser = "LoU", registrationofNewUser = "RoNU",
            showLoginofUser = "SLoU";
    private final String ioexception = "Reading a network file and got disconnected.\n" +
            "Reading a local file that was no longer available.\n" +
            "Using some stream to read data and some other process closed the stream.\n" +
            "Trying to read/write a file, but don't have permission.\n" +
            "Trying to write to a file, but disk space was no longer available.\n" +
            "There are many more examples, but these are the most common, in my experience.";
    private boolean login = false;
    @Getter
    private String usersName;

    private void incomingDataHandler(BufferedReader br) {
        try {
            String data;
            logger.info("Start of while cycle to get JSONObject");
            while ((data = br.readLine()) != null) {
                logger.debug("data that was received from while cycle: " + data);
                dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
                logger.info("Data was added into the dataQueue");
                if (dataQueue.isEmpty()) //nevedno či to fuguje, rýchlosť spracovanie je rýchlejšia
                    logger.info("Data left in dataQueue: " + dataQueue);
            }
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .PUT()", ie);
        } catch (IOException ioe) {
            logger.error(ioexception, ioe);
        } finally {
            ENDJSON = new JSONObject()
                    .put("ID", "END");
            try {
                dataQueue.put(ENDJSON);
            } catch (InterruptedException ie) {
                logger.error("Waiting thread was interrupted - .PUT()", ie);
            }
        }
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(prepojenie.getOutputStream(), true);
            in = new InputStreamReader(prepojenie.getInputStream());
            dataQueue = new LinkedBlockingQueue<>();
        } catch (IOException ioe) {
            logger.warn(ioexception, ioe);
        }
        try (BufferedReader br = new BufferedReader(in)) {

            logger.info("Creating thread to handle incoming data");
            Thread incomingDataHandlerThread = new Thread(() -> {
                incomingDataHandler(br);
            });
            incomingDataHandlerThread.setDaemon(true);
            incomingDataHandlerThread.start();
            logger.info("New thread to handle incoming data was created");

            logger.info("Start of while cycle for login/registration");
            logger.info("Switch for ID");
            enter();
            if (login) messageHandler.addToClientList(this);
            logger.info("End of while cycle for login/registration");

            logger.info("Start of while cycle which will manage incoming messages");
            while (!jsonObject.equals(ENDJSON)) { // bude prijímať správy dokým bude uživateľ online - dokončiť
                logger.info("Data taken from dataQueue: " + jsonObject);
                String msg = getStringfromJson(message);
                messageHandler.addToMessages(createJson(messagefromUser, userName, usersName, message, msg));
                messageHandler.broadcast();
            }
            logger.info("Ending while cycle for incoming messages");

        } catch (IOException ioe) {
            logger.error(ioexception, ioe);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .TAKE()", ie);
        } catch (NoSuchAlgorithmException nsae) {
            logger.error("Cryptographic algorithm is requested but is not available in the environment", nsae);
        } finally {
            logger.info("User was disconnected, deleting thread from list ,shutting down the thread...");
            messageHandler.deleteFromClientList(this);
            messageHandler.sendOnlineUsers();
        }
    }
    boolean key = true;
    private void enter() throws InterruptedException, NoSuchAlgorithmException {
        byte[] salt = null;
        while(!login) {
            switch ((jsonObject = dataQueue.take()).getString("ID")) {
                case loginofUser:
                    logger.info("Case for " + loginofUser);
                    if(key) { // prerobiť IF(KEY)!!!!!!!!!!!!
                        GetPublicKey getPublicKey = new GetPublicKey(jsonObject);
                        salt = getPublicKey.GetPK();
                        if (salt == null) logger.info("SALT IS NULL WTF");
                        out.println(createJson("LoU", "Key", salt, null, null));
                        key = false;
                    } else {
                        logger.info("Creating LoginUser class");
                        LoginUser loginUser = new LoginUser(jsonObject, salt);
                        if (login = loginUser.Login())
                            usersName = loginUser.getUserName();
                        logger.info("Name set to this thread is: " + usersName);
                        logger.info("Sending data about successful login");
                        out.println(createJson("LoU", "Attempt", login, null, null));
                        key = true;
                    }
                    break;
                case registrationofNewUser: // prerobiť IF(KEY)!!!!!!!!!!!!
                    logger.info("Case for " + registrationofNewUser);
                    if (key) {
                        salt = getPublicKey();
                        key = false;
                    } else {
                        // možnosť vzniknutia problému kedy je možná uživatela registrovať
                        // ale nastane chyba teda program si aj napriek chybe bude myslieť
                        // že sa uživatelové meno nachádzalo v databáze
                        logger.info("sCreating RegisterUser clas");
                        RegisterUser registerUser = new RegisterUser(jsonObject, salt);
                        logger.info("Sending data about successful registration");
                        out.println(createJson(registrationofNewUser, "Attempt", registerUser.Register(), null, null));
                        key = true;
                    }
                    break;
                default:
                    logger.warn("unknown Type");
                    break;
            }
        }
    }

    private byte[] getPublicKey() throws NoSuchAlgorithmException {
        byte[] salt = null;
        switch (jsonObject.getString("ID")) {
            case loginofUser:
                GetPublicKey getPublicKey = new GetPublicKey(jsonObject);
                salt = getPublicKey.GetPK();
                break;
            case registrationofNewUser:
                Hashing hashing = new Hashing();
                logger.info("Generating salt");
                salt = hashing.generateSalt();
                logger.info("Salt was generated");
                logger.info(salt[1]);
                logger.info("Sending salt to user");
                out.println(createJson("RoNU", "Key", salt, null, null));
                logger.info("Salt was send");
                break;
        }
        return salt;
    }

    private void incomingMessage() {

    }

    void cast(String message) {
        out.println(message);
    }

    private JSONObject createJson(@NonNull String IDString,
                                  @NonNull String dataOne, @NonNull Object objectOne,
                                  String dataTwo, Object objectTwo) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if (dataTwo != null && objectTwo != null)
            dataOfJsonObject.put(dataTwo, objectTwo);
        return new JSONObject()
                .put("ID", IDString)
                .put(data, dataOfJsonObject);
    }

    public String getStringfromJson(String string) throws JSONException {
        return jsonObject.getJSONObject(data).getString(string);
    }

    public JSONObject createJsonListofUsers(List<String> listofOnlineUsers) {
        JSONArray jsonArray = new JSONArray(listofOnlineUsers);
        JSONObject jsonObject = new JSONObject()
                .put("ID", showLoginofUser)
                .put(data, jsonArray);
        return jsonObject;
    }
}