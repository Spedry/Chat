package database;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Arrays;

public class RegisterUser extends AbsUser {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public RegisterUser(@NonNull JSONObject jsonObject, @NonNull byte[] publicKey) {
        super(jsonObject, publicKey);
        logger.info("RegisterUser class was created");
    }

    public boolean Register() {
        logger.info("Start of attempt to register");
        boolean registered = false;
        String jdbcURL = "jdbc:mysql://localhost:3306/chat?useSSL=false&allowPublicKeyRetrieval=true";
        logger.info("Connecting to database");
        try (
                Connection conn = DriverManager.getConnection(jdbcURL, "root", "admin");
                Statement stmt = conn.createStatement()
        ) {
            logger.info("Successfully connected to database");
            logger.info("SELECT");
            String SELECT = "SELECT UserName FROM Users";
            logger.debug("SELECT is: " + SELECT);
            ResultSet QUERY = stmt.executeQuery(SELECT);
            boolean possibleToReg = true;
            logger.info("Querying trough database");
            while(QUERY.next()) {
                logger.info("TEST");
                String UserName = QUERY.getString("UserName");
                if (UserName.equals(getUserName())) {
                    possibleToReg = false;
                    logger.info("The inserted name is already in use");
                    break;
                }
            }
            if (possibleToReg) {
                logger.info("Second hash");
                secondHash();
                logger.info("SELECT");
                SELECT = " INSERT INTO users (UserName, UserHash, PublicKey, PrivateKey)"
                        + " VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                    preparedStmt.setString(1, getUserName());
                    preparedStmt.setString(2, getUserHash());
                    preparedStmt.setBytes(3, getPublicKey());
                    preparedStmt.setBytes(4, getPrivateKey());
                    logger.info("Select: " + SELECT); //(\"" + getStringfromJson(user_name) + "\",\"" + getStringfromJson(hash) + "\")"
                    preparedStmt.execute();
                    registered = true;
                    logger.info("New user successfully registered");
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        } catch (JSONException jsone) {
            logger.error("Error with JSONObject", jsone);
        }
        return registered;
    }
}