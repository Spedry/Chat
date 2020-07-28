package sk.Spedry.server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class Server implements Runnable {
    // proste port
    static final int PORT = 50000;
    static final boolean napojenie = true;
    private Socket pripojenie;
    public static final int GB = 8;
    private static InetAddress ip = null;
    private static String hostname = null;

    public Server(Socket c) {
        pripojenie = c;
    }

    // zapnutie servera
    public static void startServer() {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Vaša aktuálna IP adresa je: " + ip + "\n" + "Meno hosta je: " + hostname + "\nServer nabieha.\nČaká na input na porte: " + PORT + "...");

            while(true) {
                sk.Spedry.server.Server myServer = new sk.Spedry.server.Server(serverConnect.accept());

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
        ObjectInputStream objectInputStream = null;

        try {
            new RegisterUser(new ObjectInputStream(pripojenie.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            vstup = new BufferedReader(new InputStreamReader(pripojenie.getInputStream()));
            messege = vstup.readLine();
            vystup = new PrintWriter(pripojenie.getOutputStream(), true);
            vystup.println("Server prijal správu: " + messege);
            System.out.println("Server prijal správu: " + messege);
            
            vystupBytes = new DataOutputStream(new BufferedOutputStream(pripojenie.getOutputStream()));
            vstupBytes = new DataInputStream(new BufferedInputStream(pripojenie.getInputStream()));
            vstupBytes.read(bytes);
            messege = new String(bytes);
            System.out.println("bytes: " + bytes);
            System.out.println("messege: " + messege);
            //FileOutputStream out = new FileOutputStream("Data/Overenie.bin");

            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}
