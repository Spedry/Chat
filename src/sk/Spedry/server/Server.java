package sk.Spedry.server;

import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {
    // Vytvoriť metodu broadcast na odoslanie správy všetkým pripojeným clientom
    private final Socket prepojenie;
    private final Logger logger = LogManager.getLogger(this.getClass());

    public Server(Socket prepojenie) {
        this.prepojenie = prepojenie;
    }

    static PrintWriter out;
    static InputStreamReader in;
    private static JSONObject jsonObject = null;
    private static LinkedBlockingQueue<JSONObject> dataQueue;
    private final String data = "Data", user_name = "Username", hash = "Password", message = "Message";
    private final String ioexception = "Reading a network file and got disconnected.\n" +
            "Reading a local file that was no longer available.\n" +
            "Using some stream to read data and some other process closed the stream.\n" +
            "Trying to read/write a file, but don't have permission.\n" +
            "Trying to write to a file, but disk space was no longer available.\n" +
            "There are many more examples, but these are the most common, in my experience.";

    private boolean login = false;

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

    private String createJson(String IDString, boolean attempt) throws JSONException {
        return new JSONObject()
                .put("ID", IDString)
                .put("Data", new JSONObject()
                        .put("Attempt", attempt))
                .toString();
    }

    @Override
    public void run() {
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
                    case "LoU":
                        logger.info("Case for LoU");
                        LoginUser loginUser = new LoginUser(jsonObject, data, user_name, hash);
                        login = loginUser.Login();
                        logger.info("Sending data about successful login");
                        out.println(createJson("LoU", login));
                        break;
                    case "RoNU":
                        logger.info("Case for RoNU");
                        RegisterUser registerUser = new RegisterUser(jsonObject, data, user_name, hash);
                        // možnosť vzniknutia problému kedy je možná uživatela registrovať
                        // ale nastane chyba teda program si aj napriek chybe bude myslieť
                        // že sa uživatelové meno nachádzalo v databáze
                        logger.info("Sending data about successful registration");
                        out.println(createJson("RoNU", registerUser.Register()));
                        break;
                    default:
                        logger.warn("unknown ID");
                        break;
                }
            }
            logger.info("End of while cycle for login/registration");
            logger.info("Start of while cycle which will manage incoming messages");
            while (true) { // bude prijímať správy dokým bude uživateľ online - dokončiť
                MessageHandler messageHandler = MessageHandler.getInstance();
                jsonObject = dataQueue.take();
                logger.info("Data taken from dataQueue: " + jsonObject);
                messageHandler.addToMessages(getStringfromJson(message));
                System.out.println(messageHandler.getMessages());
                messageHandler.multicast();
            }
        } catch (IOException ioe) {
            logger.error(ioexception, ioe);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            logger.error("Waiting thread was interrupted - .TAKE()", ie);
        }
    }

    String getStringfromJson(String string) throws JSONException {
        return jsonObject.getJSONObject(data).getString(string);
    }

    void multicast(String message) {
        out.println(message);
    }
}