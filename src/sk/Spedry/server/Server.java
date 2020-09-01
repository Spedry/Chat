package sk.Spedry.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class Server implements Runnable {
    // Vytvoriť metodu broadcast na odoslanie správy všetkým pripojeným clientom
    private final Socket prepojenie;

    public Server(Socket prepojenie) {
        this.prepojenie = prepojenie;
        //prepojenie.setSoTimeout(10*1000);
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
            } while (jsonObject.getJSONObject(D).getString(U).isBlank() &&
                    jsonObject.getJSONObject(D).getString(P).isBlank());
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
    }

    private void loadJsonObject(BufferedReader br) throws IOException, JSONException {
        String data;
        System.out.println(br.toString());
        while((data=br.readLine()) != null) {
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
                            out.println(createJson("LoU", login));
                            break;
                        case "RoNU":
                            System.out.println("register: " + jsonObject);
                            RegisterUser registerUser = new RegisterUser(jsonObject);
                            register = registerUser.Register();
                            jsonObject = null;
                            // možnosť vzniknutia problému kedy je možná uživatela registrovať
                            // ale nastane chyba teda program si aj napriek chybe bude myslieť
                            // že sa uživatelové meno nachádzalo v databáze
                            out.println(createJson("RoNU", register));
                            break;
                        default:
                            System.out.println("neznáme ID");
                            out.println(createJson("Fail", login));
                            //return;
                            break;
                    }
                } catch (JSONException jsone) {
                    jsone.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // PREROBIŤ NAJSŤ MOŽNOSŤ AKO POUŽIVAŤ IBA JEDNU METODU
    private String createJson(String IDString, boolean attempt) throws JSONException {
        return new JSONObject()
                .put("ID", IDString)
                .put("Data", new JSONObject()
                        .put("Attempt", attempt))
                        .toString();
    }
}
