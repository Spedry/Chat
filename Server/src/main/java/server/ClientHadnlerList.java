package server;

import lombok.Getter;
import java.util.ArrayList;

public class ClientHadnlerList {
    @Getter
    private ArrayList<ClientHandler> list;

    public ClientHadnlerList(){
        list = new ArrayList<>();
    }

    public void add(ClientHandler clientHandler){
        list.add(clientHandler);
    }

    public void remove(ClientHandler clientHandler){
        list.remove(clientHandler);
    }

    public int size() {
        return list.size();
    }
}
