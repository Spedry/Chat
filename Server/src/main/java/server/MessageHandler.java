package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {

    private static volatile MessageHandler instance;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private static ClientList clientList;
    private static LinkedBlockingQueue<JSONObject> messages;
    private static LinkedBlockingQueue<JSONObject> messageHistory;

    public MessageHandler() {
        clientList = new ClientList(this);
        messages = new LinkedBlockingQueue<JSONObject>();
        messageHistory = new LinkedBlockingQueue<JSONObject>(10);
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



    public void addToMessages(JSONObject message) throws InterruptedException {
        messages.put(message);
        logger.info("Message was add into queue");
        messageHistory.put(message);
        if(messageHistory.remainingCapacity() < 1) {
            messageHistory.take();
            logger.info("Message history exceed");
        }
    }

    public void addToClientList(Server server) {
        clientList.add(server);
        sendMessageHistory(server);
        logger.info("Thread was add into the arraylist");
        logger.info("Number of client in list after ADD: " + clientList.size());
    }

    public void deleteFromClientList(Server server) {
        clientList.remove(server);
        logger.info("Thread was removed from arraylist");
        logger.info("Number of client in list after REMOVE: " + clientList.size());
    }

    /*public LinkedBlockingQueue<JSONObject> getMessages() {
        return messages;
    }*/

    public void sendOnlineUsers() {
        for(Server client : clientList.getList()) {
            List<String> listofOnlineUsers = new ArrayList<String>();
            client.getUsersName();
            for(Server clientUserName : clientList.getList()) {
                String userName = clientUserName.getUsersName();
                if (userName.equals(client.getUsersName())) continue;
                listofOnlineUsers.add(userName);
            }
            client.cast(client.createJsonListofUsers(listofOnlineUsers).toString());
        }
    }

    public void sendMessageHistory(Server server) {
        if (!messageHistory.isEmpty())
        for (JSONObject jsonObject : messageHistory) {
            server.cast(jsonObject.toString());
        }
    }

    void unicast() { // one TO one

    }

    void broadcast() throws InterruptedException { // one TO all
        logger.info("Start of for cycle to send");
        String message = messages.take().toString();
        for(Server client : clientList.getList()) {
            client.cast(message);
            logger.info("Message was sent to online user");
        }
        logger.info("Message was sent to all online users");
    }

    void multicast() { //one TO specific_one's

    }
}