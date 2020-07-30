package sk.Spedry.server;

import java.net.Socket;

public class Server implements Runnable {

    private Socket prepojenie;

    public Server(Socket prepojenie) {
        this.prepojenie = prepojenie;
    }

    @Override
    public void run() {
        //zistenmie o aký typ pripojenia ide RoNU/LoU
    }
}
