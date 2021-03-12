package server;

import database.*;
import hash.Hashing;
import hash.RSA.RSA;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import shortcuts_for_M_and_V.Methods;
import shortcuts_for_M_and_V.Variables;

public class ClientHandler implements Runnable {
    private final Socket prepojenie;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final MessageHandler messageHandler;
    private PrintWriter out;
    private InputStreamReader in;
    private JSONObject jsonObject = null, ENDJSON;
    private LinkedBlockingQueue<JSONObject> dataQueue;
    private boolean login = false;
    @Getter
    private String thisThreadUserName;
    @Getter
    private int thisThreadRoomID = 0;
    private RSA rsa;

    public ClientHandler(Socket prepojenie, MessageHandler messageHandler) {
        this.prepojenie = prepojenie;
        this.messageHandler = messageHandler;
    }

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
            logger.error(Variables.IOEXCEPTION, ioe);
        } finally {
            ENDJSON = new JSONObject()
                    .put(Variables.ID, "END");
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
            logger.warn(Variables.IOEXCEPTION, ioe);
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
            logger.error(Variables.IOEXCEPTION, ioe);
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
            rsa.setOthersSidePublicKey((jsonObject = dataQueue.take()).getJSONObject(Variables.DATA).getString(Variables.KEY));
        logger.info(rsa.othersSidePublicKey);
        logger.info("KEY: " + rsa.test());
        getPrintWriter().println(Methods.createJson(Variables.RIVEST_SHAMIR_ADLEMAN, Variables.KEY, rsa.getBase64EncodedPublicKey()));
    }

    private void enter() throws InterruptedException, NoSuchAlgorithmException {
        boolean saltWasSend = false;
        byte[] salt = null;
        // TODO: CHANGE THE
        // TODO: SYSTEM WITH
        // TODO: saltWasSend PLZ
        while (!login) {
            logger.info("Switch for ID");
            switch ((jsonObject = dataQueue.take()).getString(Variables.ID)) {
                // TODO: zmeniť while (!login) na while jsonobject.equals"login successfully"
                // TODO: ktorý bude odoslaný klientom po tom čo sa úspešne prihlási
                //logger.info("Data taken from dataQueue: " + jsonObject);
                case Variables.LOGIN_OF_USER:
                    logger.info("Case for " + Variables.LOGIN_OF_USER);
                    if (!saltWasSend) { // ak bude možnosť prerobiť funguje je to ale je nu velice malá šanca chyby
                        GetterPublicKey getterPublicKey = new GetterPublicKey(jsonObject);
                        salt = getterPublicKey.getPublicKey();
                        String encodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(salt);
                        printWriter(Methods.createJson(Variables.LOGIN_OF_USER, Variables.KEY, encodedString));
                        saltWasSend = true;
                    } else {
                        logger.info("Creating LoginUser class");
                        LoginUser loginUser = new LoginUser(jsonObject, salt);
                        if (login = loginUser.login())
                            thisThreadUserName = loginUser.getUserName();
                        logger.info("Name set to this thread is: " + thisThreadUserName);
                        logger.info("Sending data about successful login");
                        printWriter(Methods.createJson(Variables.LOGIN_OF_USER, Variables.ATTEMPT, login));
                        saltWasSend = false;
                    }
                    break;
                case Variables.REGISTRATION_OF_NEW_USER: // ak bude možnosť prerobiť funguje je to ale je nu velice malá šanca chyby
                    logger.info("Case for " + Variables.REGISTRATION_OF_NEW_USER);
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
                        printWriter(Methods.createJson(Variables.REGISTRATION_OF_NEW_USER, Variables.ATTEMPT, registerUser.register()));
                        saltWasSend = false;
                    }
                    break;
                default:
                    logger.warn("UNKNOWN ID");
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
        printWriter(Methods.createJson(Variables.REGISTRATION_OF_NEW_USER, Variables.KEY, encodedString));
        logger.info("Salt was send");
        return salt;
    }

    // bude prijímať správy dokým bude uživateľ online - po odpojení uživateľa sa vygeneruje JSONObject ENDJSON ktorý ukončí cyklus
    private void incomingMessage() throws InterruptedException {
        int roomId = 0;
        while (!(jsonObject = dataQueue.take()).equals(ENDJSON)) {
            logger.info("Data taken from dataQueue: " + jsonObject);
            switch (jsonObject.getString(Variables.ID)) {
                case Variables.MESSAGE_FROM_USER:
                    logger.info("Case for " + Variables.MESSAGE_FROM_USER);
                    messageHandler.addToMessages(Methods.createJson(Variables.MESSAGE_FROM_USER,
                            Variables.USERNAME, thisThreadUserName,
                            Variables.MESSAGE, extractDataString(Variables.MESSAGE),
                            Variables.ROOM_ID, extractDataInt(Variables.ROOM_ID)));
                    messageHandler.cast();
                    break;
                case Variables.CREATE_NEW_ROOM:
                    logger.info("Case for " + Variables.CREATE_NEW_ROOM);
                    CreateNewRoom createNewRoom = new CreateNewRoom(jsonObject);
                    if (createNewRoom.createRoom()) {
                        messageHandler.createRoom(createNewRoom.getRoomId());
                        printWriter(Methods.createJson(Variables.CREATE_NEW_ROOM,
                                Variables.ROOM_ID, createNewRoom.getRoomId() ,
                                Variables.ROOM_NAME, createNewRoom.getRoomName()));
                    }
                    break; //TODO:
                case Variables.LOG_INTO_THE_ROOM:
                    logger.info("Case for " + Variables.LOG_INTO_THE_ROOM);
                    LogIntotheRoom logIntotheRoom = new LogIntotheRoom(jsonObject);
                    if (logIntotheRoom.login()) {
                        printWriter(Methods.createJson(Variables.LOG_INTO_THE_ROOM,
                                Variables.ROOM_ID, logIntotheRoom.getRoomId(),
                                Variables.ROOM_NAME, logIntotheRoom.getRoomName()));
                        }
                    roomId = logIntotheRoom.getRoomId();
                case Variables.LOAD_AN_EXISTING_ROOM:
                    logger.info("Case for " + Variables.LOAD_AN_EXISTING_ROOM);
                    if (jsonObject.getString(Variables.ID).equals(Variables.LOAD_AN_EXISTING_ROOM))
                        roomId = jsonObject.getJSONObject(Variables.DATA).getInt(Variables.ROOM_ID);
                    if (!(thisThreadRoomID == roomId)) {
                        messageHandler.deleteClientFromRoom(this);
                        thisThreadRoomID = roomId;
                        GetRoomName getRoomName = new GetRoomName(roomId);
                        printWriter(Methods.createJson(Variables.LOAD_AN_EXISTING_ROOM, Variables.ROOM_NAME, getRoomName.getRoomName()));
                        if (roomId == 0) {
                            logger.info("Room0 sending list of all online users");
                            messageHandler.sendOnlineUsersList(this);
                        }
                        else {
                            logger.info("Room" + roomId + " sending list of users in room");
                            messageHandler.sendRoomList(roomId, this);
                        }
                        messageHandler.addClientIntotheRoom(roomId, this);
                    }
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

    public String extractDataString(String typeOfData) throws JSONException {
        logger.info("TEST: " + jsonObject);
        return jsonObject.getJSONObject(Variables.DATA).getString(typeOfData);
    }

    public int extractDataInt(String typeOfData) throws JSONException {
        logger.info("TEST: " + jsonObject);
        return jsonObject.getJSONObject(Variables.DATA).getInt(typeOfData);
    }
}