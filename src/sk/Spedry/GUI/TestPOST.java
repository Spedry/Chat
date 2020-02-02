package sk.Spedry.GUI;

import jdk.swing.interop.SwingInterOpUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

public class TestPOST {
    public static void POST() {
        int ID = 0;
        String username = "Spedry";
        String massege = "Test 1... 2... 3... GO";

        Socket socket = null;
        URL URL;
        String charSet = "UTF-8";

        try {
            URL = new URL("http://localhost:8000"); // kam sa bude odosielať
            socket = new Socket("192.168.1.30", 8000);

            JSONObject json = new JSONObject(); // vystvorenie JSONObject
            json.put("ID", ID);
            json.put("userName", username);
            json.put("massege", massege); // pridanie objectov do JSONna


            /*HttpURLConnection httpcon = (HttpURLConnection)url.openConnection(); // nadviazanie pripojenia
            httpcon.setDoOutput(true);
            httpcon.setRequestMethod("POST"); // definovanie odosielanej methody
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");*/
            
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            System.out.println("JSON: " + json);
            byte[] jsonBytes = json.toString().getBytes(charSet);
            System.out.println("String: "+ json.toString());
            String s = new String(jsonBytes);
            System.out.println("Bytes: " + s);
            output.write(jsonBytes);
            //httpcon.connect();
            //String output1=httpcon.getResponseMessage();
            //System.out.println("Output: " + output1);
            output.flush();
            output.close();
        }catch(IOException ioe){
            System.out.println("Error: " + ioe);
        } catch (JSONException JSONe) {
            JSONe.printStackTrace();
        }
    }
}
