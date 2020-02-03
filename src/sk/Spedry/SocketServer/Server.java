package sk.Spedry.SocketServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    // proste port
    static final int PORT = 8000;

    static final boolean napojenie = true;

    private Socket pripojenie;

    public Server(Socket c) {
        pripojenie = c;
    }

    public static void startServer() {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server nabieha.\nČaká na input na porte: " + PORT + "...");

            while(true) {
                sk.Spedry.Server.Server myServer = new sk.Spedry.Server.Server(serverConnect.accept());

                if(napojenie) {
                    System.out.println("Pripojenie nadviazané. (" + new Date() + ")");
                }

                Thread thread = new Thread(myServer);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Chyba pri pokuse nadviadzať spojenie: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        
    }
}
