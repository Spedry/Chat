package sk.Spedry.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.Spedry.client.Client;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler implements Runnable {

    private static volatile MessageHandler instance;
    private final Logger logger = LogManager.getLogger(this.getClass());

    private MessageHandler() {
        clientList = new ArrayList<Server>();
        messages = new LinkedBlockingQueue<String>();
    }
    // Singleton pattern https://en.wikipedia.org/wiki/Singleton_pattern
    public static MessageHandler getInstance() {
        if(instance == null){ //if there is no instance available... create new one
            synchronized (MessageHandler.class) {
                if(instance == null){ // double check - Thread Safe Singleton: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
                    instance = new MessageHandler();
                }
            }
        }
        return instance;
    }

    private static ArrayList<Server> clientList;
    private static LinkedBlockingQueue<String> messages;

    public void addToMessages(String message) throws InterruptedException {
        messages.put(message);
    }

    public LinkedBlockingQueue<String> getMessages() {
        return messages;
    }

    void unicast() { // one TO one

    }

    void broadcast() { // one TO all

    }

    void multicast() throws InterruptedException { //one TO specific_one's
        logger.info("Start of for cycle to send");
        for(Server client : clientList) {
            client.multicast(messages.take());
            logger.info("Message was sent to online user");
        }
        logger.info("Message was sent to all online users");
    }

    @Override
    public void run() {

    }
}
