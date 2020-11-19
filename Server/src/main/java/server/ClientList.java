package server;

import lombok.Getter;
import java.util.ArrayList;

public class ClientList {
    @Getter
    private ArrayList<Server> list;
    MessageHandler messageHandler;

    public ClientList(MessageHandler messageHandler){
        list = new ArrayList<>();
        this.messageHandler = messageHandler;
    }

    public void add(Server server){
        list.add(server);
        messageHandler.sendOnlineUsers();
    }

    public void remove(Server server){
        list.remove(server);
        messageHandler.sendOnlineUsers();
    }

    public int size() {
        return list.size();
    }
}
