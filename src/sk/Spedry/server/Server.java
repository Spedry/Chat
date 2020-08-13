package sk.Spedry.server;

import com.mysql.jdbc.log.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class Server implements Runnable {
    // Vytvoriť metodu broadcast na odoslanie správy všetkým pripojeným clientom
    private final Socket prepojenie;

    public Server(Socket prepojenie) {
        this.prepojenie = prepojenie;
    }

    static PrintWriter out;
    static InputStreamReader in;
    private static JSONObject jsonObject = null;
    private String D = "Data", U = "Username", P = "Password";

    private boolean login = false;

    private void setLogin(BufferedReader br) {
        try {
            do {
                try {
                    loadJsonObject(br);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (JSONException jsone) {
                    jsone.printStackTrace();
                }
                /*System.out.println("json: " + jsonObject);
                System.out.println("username:" + jsonObject.getJSONObject(D).getString(U));
                System.out.println("password:" + jsonObject.getJSONObject(D).getString(P));*/
                /*if(jsonObject.length() > 0)
                    System.out.println("empty data");*/
            } while (jsonObject.getJSONObject(D).getString(U).isBlank() &&
                    jsonObject.getJSONObject(D).getString(P).isBlank());
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

    private void loadJsonObject(BufferedReader br) throws IOException, JSONException {
        /*try {
            System.out.println("načítanie");
            char[] buffer = new char[1024 * 2];
            int charsRead = 0;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(prepojenie.getInputStream()));
            while ((charsRead = bufferedReader.read(buffer)) != -1) {
                String data = new String(buffer).substring(0, charsRead);
                jsonObject = new JSONObject(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //in = new BufferedReader(new InputStreamReader(prepojenie.getInputStream()));
        /* String data;
        while ((data = in.readLine()) != null) {
            System.out.println("while readline");
            jsonObject = new JSONObject(data);
        }*/
        /*System.out.println("data before "+data);
        data = in.readLine();
        System.out.println("data after "+data);
        jsonObject = new JSONObject(data);*/
        String data;
        System.out.println(br.toString());
        while((data=br.readLine()) != "") {
            System.out.println("while data: " + data);
            jsonObject = new JSONObject(data);
            if(!jsonObject.getJSONObject(D).getString(U).isBlank() &&
                    !jsonObject.getJSONObject(D).getString(P).isBlank())
                break;
        }
        System.out.println(data);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(prepojenie.getOutputStream(), true);
            in = new InputStreamReader(prepojenie.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(in)) {

            while (!login) {
                boolean register;
                //if(jsonObject == null) {
                setLogin(br);
                //}
                //else
                try {
                    switch (Objects.requireNonNull(jsonObject).getString("ID")) {
                        case "LoU":
                            System.out.println("login: " + jsonObject);
                            LoginUser loginUser = new LoginUser(jsonObject);
                            login = loginUser.Login();
                            if (!login)
                                jsonObject = null;
                            //out.println("login");
                            break;
                        case "RoNU":
                            System.out.println("register: " + jsonObject);
                            RegisterUser registerUser = new RegisterUser(jsonObject);
                            register = registerUser.Register();
                            jsonObject = null;
                            break;
                        default:
                            System.out.println("neznáme ID");
                            return;
                    }
                } catch (JSONException jsone) {
                    jsone.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void verification() {

    }
}
