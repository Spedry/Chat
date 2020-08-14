package sk.Spedry.Tests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class test {
    public static void main(String[] args) {
        while(true) {
            try {
                Socket socket = new Socket("HERNA-MASINERIA", 50000);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                JSONObject jsonObject = new JSONObject("{\"Data\":{\"Username\":\"meno\",\"Password\":\"heslo\"},\"ID\":\"LoU\"}");
                out.println(jsonObject.toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("L");
                System.out.println(in.readLine());
                socket.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
