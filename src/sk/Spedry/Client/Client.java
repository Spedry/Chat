package sk.Spedry.Client;

import java.io.*;
import java.net.Socket;

import static sk.Spedry.Server.Server.GB;

public class Client {

    static final int PORT = 8000;

    private static Socket socket;

    public static void createSocket() {
        System.out.println("aktuálna verzia");
        try {
            socket = new Socket("localhost", PORT);
            sentMessege();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void sentMessege() {
        BufferedReader userMessege = null;
        PrintWriter sentUserMessege = null;
        BufferedReader vstup = null;
        String messege = null;

        DataOutputStream sentUserMessegeBytes = null;
        DataInputStream userMessegeBytes = null;

        byte[] bytes;

        try {
            /*userMessege = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Zadajte správu: ");
            messege = userMessege.readLine();
            sentUserMessege = new PrintWriter(socket.getOutputStream(), true);
            sentUserMessege.println(messege);
            vstup = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Server prijal správu: " + messege);*/

            userMessege = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Zadajte správu: ");
            messege = userMessege.readLine();
            bytes = messege.getBytes();
            sentUserMessegeBytes = new DataOutputStream(socket.getOutputStream());
            //sentUserMessegeBytes.write(bytes.length);
            sentUserMessegeBytes.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
