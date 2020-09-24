package sk.Spedry.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import sk.Spedry.client.Client;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler implements Runnable {

    private static volatile MessageHandler instance;
    private final Logger logger = LogManager.getLogger(this.getClass());

    public MessageHandler() {
        clientList = new ArrayList<Server>();
        messages = new LinkedBlockingQueue<JSONObject>();
    }
    // Singleton pattern https://en.wikipedia.org/wiki/Singleton_pattern
    /*public static MessageHandler getInstance() {
        if(instance == null){ //if there is no instance available... create new one
            synchronized (MessageHandler.class) {
                if(instance == null){ // double check - Thread Safe Singleton: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
                    instance = new MessageHandler();
                }
            }
        }
        return instance;
    }*/

    private static ArrayList<Server> clientList;
    private static LinkedBlockingQueue<JSONObject> messages;

    public void addToMessages(JSONObject message) throws InterruptedException {
        messages.put(message);
    }

    public void addToClientList(Server server) {
        clientList.add(server);
    }

    public void deleteFromClientList(Server server) {
        clientList.remove(server);
    }

    public LinkedBlockingQueue<JSONObject> getMessages() {
        return messages;
    }

    void unicast() { // one TO one

    }

    void broadcast() throws InterruptedException { // one TO all
        logger.info("Start of for cycle to send");
        for(Server client : clientList) {
            client.multicast(messages.take());
            logger.info("Message was sent to online user");
        }
        logger.info("Message was sent to all online users");
    }

    void multicast() { //one TO specific_one's

    }

    @Override
    public void run() {

    }
}
