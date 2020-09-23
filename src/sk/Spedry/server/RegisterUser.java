package sk.Spedry.server;

import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import java.sql.*;

public class RegisterUser extends AbsUser {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String user_name, hash;

    public RegisterUser(JSONObject jsonObject, String data, String user_name, String hash) {
        super(jsonObject, data);
        this.user_name = user_name;
        this.hash = hash;
    }

    public boolean Register() {
        logger.info("Start of attempt to register");
        boolean registered = false;
        String url = "jdbc:mysql://localhost:3306/chat?useSSL=false&allowPublicKeyRetrieval=true";
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
            String SELECT = "SELECT UserName FROM Users";
            logger.debug("SELECT is: " + SELECT);
            ResultSet QUERY = stmt.executeQuery(SELECT);
            boolean possibleToReg = true;
            logger.info("Querying trough database");
            while(QUERY.next()) {
                String UserName = QUERY.getString("UserName");
                if (UserName.equals(getStringfromJson(user_name))) {
                    possibleToReg = false;
                    logger.info("The inserted name is already in use");
                    break;
                }
            }
            if (possibleToReg) {
                SELECT = "INSERT INTO users(UserName, UserH) VALUES(\"" + getStringfromJson(user_name) + "\",\"" + getStringfromJson(hash) + "\")";
                System.out.println(SELECT);
                stmt.executeUpdate(SELECT);
                registered = true;
                logger.info("New user successfully registered");
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        }
        return registered;
    }
}