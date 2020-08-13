package sk.Spedry.gui;

import org.json.JSONObject;
import sk.Spedry.gui.controllers.LoginController;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
    static final int PORT = 50000;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader inputReader;
    private String input;
    private static volatile Client instance;

    private Client() {
        String hostname = null;
        try {
            InetAddress ip = null;
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new Socket(hostname, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Singleton pattern https://en.wikipedia.org/wiki/Singleton_pattern
    public static Client getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new Client();
        }

        return instance;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                while (input == null) {
                    Thread.sleep(1000);
                }
                inputReader = new BufferedReader(new StringReader(input));
                while ((input = inputReader.readLine()) != null) {
                    getPrintWriter().println(input);
                }
                if(in.ready()) break;
            }
            System.out.println("success");
            //socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
