package database;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Variables;

import java.sql.*;

public class GetterPublicKey extends LoginToMySQL {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String userName;

    public GetterPublicKey(@NonNull JSONObject jsonObject) {
        this.userName = jsonObject.getJSONObject(Variables.DATA).getString(Variables.USERNAME);
        logger.info("GetterPublicKey class was created");
    }

    public byte[] getPublicKey(){
        logger.info("Start of attempt to get public key");
        logger.info("Connecting to database");
        byte[] salt = null;
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT PublicKey, UserName FROM Users WHERE UserName = '" + userName + "'";
            logger.debug("SELECT is: " + SELECT);
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                logger.info("Successfully connected to database");
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                // Pidať možnosť neexistujúceho mena ale aj hesla pre pripad...
                logger.info("Querying trough database");
                while (QUERY.next()) {
                    if (QUERY.getString("UserName").equals(userName)) {
                        salt = QUERY.getBytes("PublicKey");
                        break;
                    }
                }
            }  catch (JSONException jsone) {
                logger.error("Error with JSONObject", jsone);
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return salt;
    }
}
