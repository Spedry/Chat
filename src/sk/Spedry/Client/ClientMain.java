package sk.Spedry.Client;


import static sk.Spedry.Client.Client.createSocket;

public class ClientMain {
    public static void main(String[] args) {
        while (true) {
            createSocket();
        }
    }
}
