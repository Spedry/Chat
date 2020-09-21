package sk.Spedry.server;

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
    private final Logger logger;

    public Server(Socket prepojenie, Logger logger) {
        this.prepojenie = prepojenie;
        this.logger = logger;
    }

    static PrintWriter out;
    static InputStreamReader in;
    private static JSONObject jsonObject = null;
    private static LinkedBlockingQueue<JSONObject> dataQueue;
    private final String data = "Data", user_name = "Username", hash = "Password", message = "Message";

    private boolean login = false;

    private boolean isJsonObjectEmpty() throws JSONException {
        boolean bool = true;
        if (!jsonObject.getJSONObject(this.data).getString(user_name).isBlank() &&
                !jsonObject.getJSONObject(this.data).getString(hash).isBlank() ||
                !jsonObject.getJSONObject(this.data).getString(message).isBlank()) {
            logger.info("JSONObject for login/register or message was correct");
            bool = false;
        }
        return bool;
    }

    private void incomingDataHandler(BufferedReader br) throws IOException, JSONException, InterruptedException {
        String data;
        logger.info("Start of while cycle to get JSONObject");
        while ((data = br.readLine()) != null) {
            logger.debug("data that was received from while cycle: " + data);
            dataQueue.put(new JSONObject(data)); // .put by mohlo byť nahradené za .add keďže dataQueue nieje omezená
            logger.info("Data was added into the dataQueue");
            System.out.println(data);
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
            ioe.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(in)) {
            Thread incomingDataHandlerThread = new Thread(() -> {
                try {
                    incomingDataHandler(br);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            logger.info("New thread to handle incoming data was created");
            incomingDataHandlerThread.setDaemon(true);
            incomingDataHandlerThread.start();
            logger.info("Start of while cycle for login/registration");
            while (!login) {
                logger.info("Switch for ID");
                Thread.sleep(5000);
                switch ((jsonObject = dataQueue.take()).getString("ID")) {
                    case "LoU":
                        logger.info("Case for LoU");
                        LoginUser loginUser = new LoginUser(jsonObject, logger, data, user_name, hash);
                        login = loginUser.Login();
                        logger.info("Sending data about successful login");
                        out.println(createJson("LoU", login));
                        break;
                    case "RoNU":
                        boolean registered;
                        logger.info("Case for RoNU");
                        RegisterUser registerUser = new RegisterUser(jsonObject, logger, data, user_name, hash);
                        registered = registerUser.Register();
                        // možnosť vzniknutia problému kedy je možná uživatela registrovať
                        // ale nastane chyba teda program si aj napriek chybe bude myslieť
                        // že sa uživatelové meno nachádzalo v databáze
                        logger.info("Sending data about successful registration");
                        out.println(createJson("RoNU", registered));
                        break;
                    default:
                        logger.warn("unknown ID");
                        break;
                }
            }
            logger.info("End of while cycle for login/registration");
            logger.info("Start of while cycle which will manage incoming messages");
            while (true) { // bude prijímať správy dokým bude uživateľ online - dokončiť
                logger.info("test");
                jsonObject = dataQueue.take();
                MessageHandler messageHandler = MessageHandler.getInstance();
                logger.info("Received data: " + jsonObject);
                messageHandler.addToMessages(getStringfromJson(message));
                System.out.println(messageHandler.getMessages());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    String getStringfromJson(String string) throws JSONException {
        return jsonObject.getJSONObject(data).getString(string);
    }

    void multicast(String message) {
        out.println(message);
    }
}