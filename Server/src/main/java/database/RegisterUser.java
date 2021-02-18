package database;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import java.sql.*;

public class RegisterUser extends AbsUser {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public RegisterUser(@NonNull JSONObject jsonObject, @NonNull byte[] publicKey) {
        super(jsonObject, publicKey);
        logger.info("RegisterUser class was created");
    }

    public boolean Register() {
        logger.info("Start of attempt to register");
        logger.info("Connecting to database");
        boolean registered = false;
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT UserName FROM Users";
            logger.debug("SELECT is: " + SELECT);
            boolean possibleToReg = true;
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                logger.info("Querying trough database");
                                while(QUERY.next()) {
                    String UserName = QUERY.getString("UserName");
                    if (UserName.equals(getUserName())) {
                        possibleToReg = false;
                        logger.info("The inserted name is already in use");
                        break;
                    }
                }
            }  catch (JSONException jsone) {
                logger.error("Error with JSONObject", jsone);
            }
            logger.info("Creating SELECT");
            SELECT = "INSERT INTO users (UserName, UserHash, PublicKey, PrivateKey) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                if (possibleToReg) {
                    logger.info("Second hash");
                    secondHash();
                    logger.debug("SELECT is: " + SELECT);
                    logger.info(getUserName() + getUserHash());
                    preparedStmt.setString(1, getUserName());
                    preparedStmt.setString(2, getUserHash());
                    preparedStmt.setBytes(3, getPublicKey());
                    preparedStmt.setBytes(4, getPrivateKey());
                    preparedStmt.execute();
                    registered = true;
                    logger.info("New user successfully registered");
                }
            }
        }catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return registered;
    }
}