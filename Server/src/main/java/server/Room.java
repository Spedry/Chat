package server;

import lombok.Getter;
import org.json.JSONObject;
import java.util.concurrent.LinkedBlockingQueue;

public class Room {
    @Getter
    private int roomID;
    @Getter
    private LinkedBlockingQueue<JSONObject> messageHistory;
    @Getter
    private ClientHadnlerList clientsInRoom;

    public Room(int roomID) {
        this.roomID = roomID;
        messageHistory = new LinkedBlockingQueue<JSONObject>(10);
        clientsInRoom = new ClientHadnlerList();
    }

    public void addClient(ClientHandler clientHandler) {
        clientsInRoom.add(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler) {
        clientsInRoom.remove(clientHandler);
    }

    public void addMessagetoHistory(JSONObject message) throws InterruptedException {
        messageHistory.put(message);
        if(messageHistory.remainingCapacity() < 1) {
            messageHistory.take();
        }
    }
}
