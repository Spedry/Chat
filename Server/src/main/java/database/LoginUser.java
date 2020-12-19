package database;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class LoginUser extends AbsUser {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public LoginUser(JSONObject jsonObject, byte[] publicKey) {
        super(jsonObject, publicKey);
        logger.info("LoginUser class was created");
    }

    public boolean Login() {
        logger.info("Start of attempt to login");
        boolean login = false;
        String jdbcURL = "jdbc:mysql://localhost:3306/chat?useSSL=false";
        logger.info("Connecting to database");
        try (
                Connection conn = DriverManager.getConnection(jdbcURL, "root", "admin");
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
                if (UserName.equals(getUserName())) {
                    if (pass(UserID, stmt, getUserHash())) {
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

    private boolean pass(int UserID, Statement stmt, String pass) throws SQLException {
        String SELECT = "SELECT UserHash FROM Users WHERE UserID =" + UserID;
        logger.info("SELECT is: " + SELECT);
        ResultSet QUERY = stmt.executeQuery(SELECT);
        while (QUERY.next()) {
            if(QUERY.getString("UserHash").equals(pass))
                return true;
        }
        return false;
    }
}