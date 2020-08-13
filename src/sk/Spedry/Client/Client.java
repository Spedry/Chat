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

    /*public static boolean isSocketAlive() {
        boolean isAlive = false;
        ipPort();
        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(ip, PORT);
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 5000;

        try {
            socket.connect(socketAddress, timeout);
            socket.close();
            isAlive = true;

        } catch (SocketTimeoutException ste) {
            System.out.println("SocketTimeoutException " + ip + ":" + PORT + ". " + ste.getMessage());
        } catch (IOException ioe) {
            System.out.println("IOException - Unable to connect to " + ip + ":" + PORT + ". " + ioe.getMessage());
        }
        return isAlive;
    }*/

    /*private static void sentMessege() {
        BufferedReader userMessege = null;
        PrintWriter sentUserMessege = null;
        BufferedReader vstup = null;
        String messege = null;

        DataOutputStream sentUserMessegeBytes = null;
        DataInputStream userMessegeBytes = null;

        byte[] bytes;

        try {
            userMessege = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Zadajte správu: ");
            messege = userMessege.readLine();
            sentUserMessege = new PrintWriter(socket.getOutputStream(), true);
            sentUserMessege.println(messege);
            vstup = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Server prijal správu: " + messege);

            userMessege = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Zadajte správu: ");
            messege = userMessege.readLine();
            bytes = messege.getBytes();
            sentUserMessegeBytes = new DataOutputStream(socket.getOutputStream());
            sentUserMessegeBytes.write(bytes.length);
            sentUserMessegeBytes.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}