package server;

import com.mysql.cj.xdevapi.JsonArray;
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
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;

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
    private JSONObject jsonObject = null;
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

    private void incomingDataHandler(BufferedReader br) throws IOException, JSONException, InterruptedException {
        String data;
        logger.info("Start of while cycle to get JSONObject");
        while ((data = br.readLine()) != null) {
            logger.debug("data that was received from while cycle: " + data);
            dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            logger.info("Data was added into the dataQueue");
            if (dataQueue.isEmpty()) //nevedno či to fuguje, rýchlosť spracovanie je rýchlejšia
                logger.info("Data left in dataQueue: " + dataQueue);
        }
    }

    private JSONObject createJson(String IDString,
                              String dataOne, Object objectOne,
                              String dataTwo, Object objectTwo) throws JSONException {
        JSONObject dataOfJsonObject = new JSONObject()
                .put(dataOne, objectOne);
        if ( dataTwo != null && objectTwo != null)
                dataOfJsonObject.put(dataTwo, objectTwo);
        return new JSONObject()
                .put("ID", IDString)
                .put(data, dataOfJsonObject);
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("User was disconnected, deletin thread from list ,shuting thread...");

            messageHandler.deleteFromClientList(this);
            messageHandler.sendOnlineUsers();
            try {
                out.close();
                in.close();
                prepojenie.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        try {
            out = new PrintWriter(prepojenie.getOutputStream(), true);
            in = new InputStreamReader(prepojenie.getInputStream());
            dataQueue = new LinkedBlockingQueue<>();
        } catch (IOException ioe) {
            logger.info(ioexception, ioe);
        }
        try (BufferedReader br = new BufferedReader(in)) {
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
            incomingDataHandlerThread.setDaemon(true);
            incomingDataHandlerThread.start();
            logger.info("Start of while cycle for login/registration");
            while (!login) {
                logger.info("Switch for ID");
                switch ((jsonObject = dataQueue.take()).getString("ID")) {
                    case loginofUser:
                        logger.info("Case for " + loginofUser);
                        LoginUser loginUser = new LoginUser(jsonObject, data, userName, hash);
                        if (login = loginUser.Login())
                            usersName = loginUser.getUsersName();
                        logger.info("Name set to this thread is: " + usersName);
                        //login = true;
                        logger.info("Sending data about successful login");
                        out.println(createJson("LoU", "Attempt", login, null, null));
                        break;
                    case registrationofNewUser:
                        logger.info("Case for " + registrationofNewUser);
                        RegisterUser registerUser = new RegisterUser(jsonObject, data, userName, hash);
                        // možnosť vzniknutia problému kedy je možná uživatela registrovať
                        // ale nastane chyba teda program si aj napriek chybe bude myslieť
                        // že sa uživatelové meno nachádzalo v databáze
                        logger.info("Sending data about successful registration");
                        out.println(createJson(registrationofNewUser, "Attempt", registerUser.Register(), null, null));
                        break;
                    default:
                        logger.warn("unknown ID");
                        break;
                }
            }
            logger.info("End of while cycle for login/registration");

            if (login) messageHandler.addToClientList(this);
            //cast(createJson(SLoU, userName, usersName, null, null).toString());
            //messageHandler.addToMessages(createJson(SLoU, userName, usersName, null, null));
            //messageHandler.broadcast();

            messageHandler.sendOnlineUsers();

            logger.info("Start of while cycle which will manage incoming messages");
            while (true) { // bude prijímať správy dokým bude uživateľ online - dokončiť
                jsonObject = dataQueue.take();
                logger.info("Data taken from dataQueue: " + jsonObject);
                String msg = getStringfromJson(message);
                messageHandler.addToMessages(createJson(messagefromUser, userName, usersName, message, msg));
                //System.out.println(messageHandler.getMessages());
                messageHandler.broadcast();
            }
        } catch (IOException ioe) {
            logger.error(ioexception, ioe);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .TAKE()", ie);
        }
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

    void cast(String message) {
        out.println(message);
    }
}