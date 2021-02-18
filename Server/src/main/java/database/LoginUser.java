package database;

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
        logger.info("Connecting to database");
        boolean login = false;
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT UserID, UserName FROM Users";
            logger.info("SELECT is: " + SELECT);
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                logger.info("Querying trough database");
                while (QUERY.next()) {
                    int UserID = QUERY.getInt("UserID");
                    String UserName = QUERY.getString("UserName");
                    if (UserName.equals(getUserName())) {
                        if (compareHashes(UserID, preparedStmt, getUserHash())) {
                            logger.info("Login attempt succeed");
                            login = true;
                        } else {
                            logger.info("Login attempt failed");
                            login = false;
                        }
                        break;
                    }
                }
            }  catch (JSONException jsone) {
                logger.error("Error with JSONObject", jsone);
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return login;
    }

    private boolean compareHashes(int userID, PreparedStatement preparedStmt, String pass) throws SQLException {
        logger.info("Creating SELECT");
        String SELECT = "SELECT UserHash FROM Users WHERE UserID = " + userID;
        logger.info("SELECT is: " + SELECT);
        ResultSet QUERY = preparedStmt.executeQuery(SELECT);
        while (QUERY.next()) {
            if(QUERY.getString("UserHash").equals(pass))
                return true;
        }
        return false;
    }
}