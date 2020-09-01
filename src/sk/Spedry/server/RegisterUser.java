package sk.Spedry.server;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;

public class RegisterUser {

    private JSONObject jsonObject;

    public RegisterUser(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean Register() {
        boolean registered = false;
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false&allowPublicKeyRetrieval=true";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (
                Connection conn = DriverManager.getConnection(url, "root", "admin");
                Statement stmt = conn.createStatement();
        ) {
            String SELECT = "select UserName from Users";
            ResultSet QUARY = stmt.executeQuery(SELECT);
            boolean possibleToReg = true;
            while(QUARY.next()) {
                String UserName = QUARY.getString("UserName");
                if (UserName.equals(jsonObject.getJSONObject("Data").getString("Username"))) {
                    possibleToReg = false;
                    break;
                }
            }
            if (possibleToReg) {
                SELECT = "insert into users(UserName, UserH) values(\"" + jsonObject.getJSONObject("Data").getString("Username") +
                        "\",\"" + jsonObject.getJSONObject("Data").getString("Password") + "\")";
                System.out.println(SELECT);
                stmt.executeUpdate(SELECT);
                registered = true;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }
        return registered;
    }
}