package sk.Spedry.server;

import sk.Spedry.client.Client;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler implements Runnable {

    private static volatile MessageHandler instance;

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

    private void unicast() { // one TO one

    }

    private void broadcast() { // one TO all

    }

    private void multicast() throws InterruptedException { //one TO specific_one's
        for(Server client : clientList)
            client.multicast(messages.take());
    }

    @Override
    public void run() {

    }
}
