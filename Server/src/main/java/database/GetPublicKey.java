package database;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.sql.*;

public class GetPublicKey {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String userName;

    public GetPublicKey(@NonNull JSONObject jsonObject) {
        this.userName = jsonObject.getJSONObject("Data").getString("Username");
    }

    public byte[] GetPK() {
        logger.info("Start of attempt to get public key");
        byte[] salt = null;
        String jdbcURL = "jdbc:mysql://localhost:3306/chat?useSSL=false&allowPublicKeyRetrieval=true";
        logger.info("Connecting to database");
        try (
                Connection conn = DriverManager.getConnection(jdbcURL, "root", "admin");
                Statement stmt = conn.createStatement()
        ) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT PublicKey, UserName FROM Users WHERE UserName = \'" + userName + "\'";
            logger.debug("SELECT is: " + SELECT);
            ResultSet QUERY = stmt.executeQuery(SELECT);
            while (QUERY.next()) {
                logger.info(QUERY.getString("UserName"));
                if(QUERY.getString("UserName").equals(userName)) {
                    salt = QUERY.getBytes("PublicKey");
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        }
        return salt;
    }
}
