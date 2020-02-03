package sk.Spedry.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class Server implements Runnable {
    // proste port
    static final int PORT = 8000;
    static final boolean napojenie = true;
    private Socket pripojenie;
    public static final int GB = 8;

    public Server(Socket c) {
        pripojenie = c;
    }

    // zapnutie servera
    public static void startServer() {
        System.out.println("aktuálna verzia");
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

    @Override
    public void run() {
        BufferedReader vstup = null;
        PrintWriter vystup = null;
        String messege = null;
        byte[] bytes = new byte[1024 * GB];
        DataInputStream vstupBytes = null;
        DataOutputStream vystupBytes = null;

        try {
            /*vstup = new BufferedReader(new InputStreamReader(pripojenie.getInputStream()));
            messege = vstup.readLine();
            vystup = new PrintWriter(pripojenie.getOutputStream(), true);
            vystup.println("Server prijal správu: " + messege);
            System.out.println("Server prijal správu: " + messege);*/

            vystupBytes = new DataOutputStream(new BufferedOutputStream(pripojenie.getOutputStream()));
            vstupBytes = new DataInputStream(new BufferedInputStream(pripojenie.getInputStream()));
            vstupBytes.read(bytes);
            messege = new String(bytes);
            System.out.println(bytes);
            System.out.println(messege);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
