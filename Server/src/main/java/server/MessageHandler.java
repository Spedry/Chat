package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {

    private static volatile MessageHandler instance;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private static ClientHadnlerList clientHandlerList;
    private static LinkedBlockingQueue<JSONObject> messages;
    private static LinkedBlockingQueue<JSONObject> messageHistory;

    public MessageHandler() {
        clientHandlerList = new ClientHadnlerList();
        messages = new LinkedBlockingQueue<JSONObject>();
        messageHistory = new LinkedBlockingQueue<JSONObject>(10);
    }

    public void addToMessages(JSONObject message) throws InterruptedException {
        messages.put(message);
        logger.info("Message was add into queue");
        messageHistory.put(message);
        if(messageHistory.remainingCapacity() < 1) {
            messageHistory.take();
            logger.info("Message history exceed");
        }
    }

    public void addToClientList(ClientHandler clientHandler) {
        clientHandlerList.add(clientHandler);
        sendOnlineUser(clientHandler, "SOU");
        sendMessageHistory(clientHandler);
        logger.info("Thread was add into the arraylist");
        logger.info("Number of client in list after ADD: " + clientHandlerList.size());
    }

    public void deleteFromClientList(ClientHandler clientHandler) {
        clientHandlerList.remove(clientHandler);
        sendOnlineUser(clientHandler, "DOU");
        logger.info("Thread was removed from arraylist");
        logger.info("Number of client in list after REMOVE: " + clientHandlerList.size());
    }

    public void sendOnlineUsersList(ClientHandler clientHandler) {
        List<String> listofOnlineUsers = new ArrayList<>();
        for(ClientHandler client : clientHandlerList.getList()) {
            if (client.equals(clientHandler)) continue;
            listofOnlineUsers.add(client.getUsersName());
        }
        clientHandler.cast(clientHandler.createJsonListofUsers(listofOnlineUsers));
    }

    public void sendOnlineUser(ClientHandler clientHandler, String ID) {
        for(ClientHandler client : clientHandlerList.getList()) {
            if (client.equals(clientHandler)) continue;
            client.cast(clientHandler.createJson(ID, "Username", clientHandler.getUsersName(), null, null));
        }
    }

    public void sendMessageHistory(ClientHandler clientHandler) {
        if (!messageHistory.isEmpty())
        for (JSONObject jsonObject : messageHistory) {
            clientHandler.cast(jsonObject);
        }
    }

    void unicast() { // one TO one

    }

    void broadcast() throws InterruptedException { // one TO all
        logger.info("Start of for cycle to send");
        JSONObject message = messages.take();
        for(ClientHandler client : clientHandlerList.getList()) {
            client.cast(message);
            logger.info("Message was sent to online user");
        }
        logger.info("Message was sent to all online users");
    }

    void multicast() { //one TO specific_one's

    }
}