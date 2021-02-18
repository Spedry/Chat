package server;

import database.GetterPublicKey;
import database.LoginUser;
import database.RegisterUser;
import hash.Hashing;
import hash.RSA.RSA;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
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
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;

public class ClientHandler implements Runnable {
    private final Socket prepojenie;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final MessageHandler messageHandler;
    public ClientHandler(Socket prepojenie, MessageHandler messageHandler) {
        this.prepojenie = prepojenie;
        this.messageHandler = messageHandler;
    }

    private PrintWriter out;
    private InputStreamReader in;
    private JSONObject jsonObject = null, ENDJSON;
    private LinkedBlockingQueue<JSONObject> dataQueue;
    private final String   data = "Data", userName = "Username", hash = "Password", message = "Message",
            messagefromUser = "MfU", loginofUser = "LoU", registrationofNewUser = "RoNU",
            showLoginofOnlineUser = "SLoOU";
    private final String ioexception = "Reading a network file and got disconnected.\n" +
            "Reading a local file that was no longer available.\n" +
            "Using some stream to read data and some other process closed the stream.\n" +
            "Trying to read/write a file, but don't have permission.\n" +
            "Trying to write to a file, but disk space was no longer available.\n" +
            "There are many more examples, but these are the most common, in my experience.";
    private boolean login = false;
    @Getter
    private String usersName;
    private RSA rsa;

    private void incomingDataHandler(@NonNull BufferedReader br) {
        try {
            String data;
            if ((data = br.readLine()) != null) {
                logger.debug("Data for RSA: " + data);
                dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            }

            logger.info("Start of while cycle to get JSONObject");
            while ((data = br.readLine()) != null) {
                logger.info("Data was received"); //Data are in hash format
                dataQueue.put(new JSONObject(rsa.decrypt(data))); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
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
            Thread incomingDataHandlerThread = new Thread(() -> incomingDataHandler(br));
            incomingDataHandlerThread.setDaemon(true);
            incomingDataHandlerThread.start();
            logger.info("New thread to handle incoming data was created");

            logger.info("Receiving RSA public key");
            setRSAKey();
            logger.info("Key was received and successfully set");

            logger.info("Start of while cycle for login/registration");
            enter();
            logger.info("End of while cycle for login/registration");

            logger.info("Start of while cycle which will manage incoming messages");
            incomingMessage();
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
            logger.info("User was disconnected, deleting thread from list, shutting down the thread...");
            messageHandler.deleteFromClientList(this);
        }
    }

    private void setRSAKey() throws InterruptedException, NoSuchAlgorithmException {
        rsa = new RSA();
        logger.info(rsa.othersSidePublicKey);
        if(rsa.isOthersSidePublicKeyNull())
            rsa.setOthersSidePublicKey((jsonObject = dataQueue.take()).getJSONObject("Data").getString("Key"));
        logger.info(rsa.othersSidePublicKey);
        logger.info("KEY: " + rsa.test());
        getPrintWriter().println(createJson("RSA", "Key", rsa.getBase64EncodedPublicKey(), null, null));
    }

    private void enter() throws InterruptedException, NoSuchAlgorithmException {
        boolean saltWasSend = false;
        byte[] salt = null;
        // TODO: CHANGE THE
        // TODO: SYSTEM WITH
        // TODO: saltWasSend PLZ
        while (!login) {
            logger.info("Switch for ID");
            switch ((jsonObject = dataQueue.take()).getString("ID")) {
                // TODO: zmeniť while (!login) na while jsonobject.equals"login successfully"
                // TODO: ktorý bude odoslaný klientom po tom čo sa úspešne prihlási
                //logger.info("Data taken from dataQueue: " + jsonObject);
                case loginofUser:
                    logger.info("Case for " + loginofUser);
                    if (!saltWasSend) { // ak bude možnosť prerobiť funguje je to ale je nu velice malá šanca chyby
                        GetterPublicKey getterPublicKey = new GetterPublicKey(jsonObject);
                        salt = getterPublicKey.getPublicKey();
                        String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(salt);
                        printWriter(createJson("LoU", "Key", encodedString, null, null));
                        saltWasSend = true;
                    } else {
                        logger.info("Creating LoginUser class");
                        LoginUser loginUser = new LoginUser(jsonObject, salt);
                        if (login = loginUser.Login())
                            usersName = loginUser.getUserName();
                        logger.info("Name set to this thread is: " + usersName);
                        logger.info("Sending data about successful login");
                        printWriter(createJson("LoU", "Attempt", login, null, null));
                        saltWasSend = false;
                    }
                    break;
                case registrationofNewUser: // ak bude možnosť prerobiť funguje je to ale je nu velice malá šanca chyby
                    logger.info("Case for " + registrationofNewUser);
                    if (!saltWasSend) {
                        salt = getPublicKey();
                        saltWasSend = true;
                    } else {
                        // možnosť vzniknutia problému kedy je možná uživatela registrovať
                        // ale nastane chyba teda program si aj napriek chybe bude myslieť
                        // že sa uživatelové meno nachádzalo v databáze
                        logger.info("Creating RegisterUser class");
                        RegisterUser registerUser = new RegisterUser(jsonObject, salt);
                        logger.info("Sending data about successful registration");
                        printWriter(createJson(registrationofNewUser, "Attempt", registerUser.Register(), null, null));
                        saltWasSend = false;
                    }
                    break;
                default:
                    logger.warn("unknown ID");
                    break;
            }
        }
        logger.info("Adding user into client list");
        if (login) messageHandler.addToClientList(this);
        logger.info("Sending list of online users");
        messageHandler.sendOnlineUsersList(this);
    }

    private byte[] getPublicKey() throws NoSuchAlgorithmException {
        byte[] salt;
        Hashing hashing = new Hashing();
        logger.info("Generating salt");
        salt = hashing.generateSalt();
        String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(salt);
        logger.info("Salt was generated");
        logger.info("Sending salt to user");
        printWriter(createJson("RoNU", "Key", encodedString, null, null));
        logger.info("Salt was send");
        return salt;
    }

    // bude prijímať správy dokým bude uživateľ online - po odpojení uživateľa sa vygeneruje JSONObject ENDJSON ktorý ukončí cyklus
    private void incomingMessage() throws InterruptedException {
        while (!(jsonObject = dataQueue.take()).equals(ENDJSON)) {
            logger.info("Data taken from dataQueue: " + jsonObject);
            switch (jsonObject.getString("ID")) {
                case messagefromUser:
                    String msg = extractData(message);
                    messageHandler.addToMessages(createJson(messagefromUser, userName, usersName, message, msg));
                    messageHandler.broadcast();
                    break;
                case "LR":
                    logger.info(jsonObject.getJSONObject("Data").getInt("RoomID"));

                    break;
                default:
                    logger.warn("unknown ID");
                    break;

            }
        }
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    private void printWriter(JSONObject data) {
        String stringData =  String.valueOf(data);
        String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(rsa.encrypt(stringData));
        getPrintWriter().println(encodedString);
    }

    void cast(JSONObject message) {
        printWriter(message);
    }

    public JSONObject createJson(@NonNull String IDString,
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

    public String extractData(String typeOfData) throws JSONException {
        logger.info("TEST: " + jsonObject);
        return jsonObject.getJSONObject(data).getString(typeOfData);
    }

    public JSONObject createJsonListofUsers(List<String> listofOnlineUsers) {
        JSONArray jsonArray = new JSONArray(listofOnlineUsers);
        JSONObject jsonObject = new JSONObject()
                .put("ID", showLoginofOnlineUser)
                .put(data, jsonArray);
        logger.info(jsonObject);
        return jsonObject;
    }
}