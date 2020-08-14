package main.java.sk.Spedry.Tests;

import org.json.JSONException;
import org.json.JSONObject;
import sk.Spedry.client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class test2 {
    public static Client client;
    public static Socket socket;
    public static PrintWriter out;
    public static BufferedReader in;

    public static void main(String[] args) throws JSONException {
        //java.awt.EventQueue.invokeLater(() -> {
            //socket = Client.createSocket(); //bolo odstranené

            try {
                //socket.setKeepAlive(true);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        //});

        for (int i = 0; i < 10; i++) {
            JSONObject jsonObject = new JSONObject("{\"Data\":{\"Username\":\"meno " + i + " \",\"Password\":\"heslo " + i + "\"},\"ID\":\"LoU\"}");
            out.println(jsonObject.toString());
        }



    }
}
