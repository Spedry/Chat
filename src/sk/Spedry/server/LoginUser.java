package sk.Spedry.server;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;

public class LoginUser {

    private JSONObject jsonObject;

    public LoginUser(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean Login() {
        boolean login = false;
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (
                Connection conn = DriverManager.getConnection(url, "root", "admin");
                Statement stmt = conn.createStatement();
        ) {
            String SELECT = "select UserID, UserName from Users";
            //logger.debug username - čo obsahuje
            //jeden logger na celí program
            ResultSet QUARY = stmt.executeQuery(SELECT);

            while(QUARY.next()) {
                int UserID = QUARY.getInt("UserID");
                String UserName = QUARY.getString("UserName");
                if (UserName.equals(jsonObject.getJSONObject("Data").getString("Username"))) {
                    if (pass(UserID, stmt, jsonObject.getJSONObject("Data").getString("Password"))) {
                        System.out.println("prihlásenie prebehlo úspešne");
                        //odošli že príhlásenie prebehlo úspešne a umožni uživatelovi vstúpiť do APP
                        login = true;
                    }
                    else {
                        System.out.println("prihlásenie neprebehlo úspešne");
                        //odošli že príhlásenie neprebehlo úspešne a neumožni uživatelovi vstúpiť do APP
                        login = false;
                    }
                    break;
                }
            }
        } catch (SQLException sqle) {
            System.out.println("Chyba pri pripájaní na SQL server");
            sqle.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        return login;
    }

    private static boolean pass(int UserID, Statement stmt, String pass) throws SQLException {
        String SELECT = "select UserH from Users where UserID =" + UserID;
        ResultSet QUARY = stmt.executeQuery(SELECT);
        while (QUARY.next()) {
            if(QUARY.getString("UserH").equals(pass))
                return true;
        }
        return false;
    }
}