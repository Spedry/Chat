package sk.Spedry.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    static final int PORT = 50000;

    private static Socket socket;

    public static Socket createSocket() {
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
        return socket;
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