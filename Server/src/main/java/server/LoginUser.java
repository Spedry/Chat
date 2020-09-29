package server;

import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import java.sql.*;

public class LoginUser extends AbsUser {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String user_name, hash;

    public LoginUser(JSONObject jsonObject, String data, String user_name, String hash) {
        super(jsonObject, data);
        this.user_name = user_name;
        this.hash = hash;
    }

    public boolean Login() {
        logger.info("Start of attempt to login");
        boolean login = false;
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            logger.info("Driver has been registered with the DriverManager");
        } catch (ClassNotFoundException cnfe) {
            logger.error("No class with specified name could be found.", cnfe);
        }
        logger.info("Connecting to database");
        try (
                Connection conn = DriverManager.getConnection(url, "root", "admin");
                Statement stmt = conn.createStatement()
        ) {
            logger.info("Successfully connected to database");
            String SELECT = "SELECT UserID, UserName FROM Users";
            logger.debug("SELECT is: " + SELECT);
            ResultSet QUERY = stmt.executeQuery(SELECT);
            logger.info("Querying trough database");
            while(QUERY.next()) {
                int UserID = QUERY.getInt("UserID");
                String UserName = QUERY.getString("UserName");
                if (UserName.equals(getStringfromJson(user_name))) {
                    if (pass(UserID, stmt, getStringfromJson(hash))) {
                        logger.info("Login attempt succeed");
                        //odošli že príhlásenie prebehlo úspešne a umožni uživatelovi vstúpiť do APP
                        login = true;
                    }
                    else {
                        logger.info("Login attempt failed");
                        //odošli že príhlásenie neprebehlo úspešne a neumožni uživatelovi vstúpiť do APP
                        login = false;
                    }
                    break;
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        }
        return login;
    }

    private static boolean pass(int UserID, Statement stmt, String pass) throws SQLException {
        String SELECT = "SELECT UserH FROM Users WHERE UserID =" + UserID;
        ResultSet QUERY = stmt.executeQuery(SELECT);
        while (QUERY.next()) {
            if(QUERY.getString("UserH").equals(pass))
                return true;
        }
        return false;
    }
}