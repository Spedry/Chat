package database;

import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.*;

public class LogIntotheRoom extends AbsRoom {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public LogIntotheRoom(@NonNull JSONObject jsonObject) {
        super(jsonObject);
        logger.info("LoginUser class was created");
    }

    public boolean login() {
        logger.info("Start of attempt to login");
        logger.info("Connecting to database");
        boolean login = false;
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT RoomID, RoomName FROM rooms";
            logger.info("SELECT is: " + SELECT);
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                logger.info("Querying trough database");
                while (QUERY.next()) {
                    setRoomId(QUERY.getInt("RoomID"));
                    String RoomName = QUERY.getString("RoomName");
                    if (RoomName.equals(getRoomName())) {
                        if (comparePasswords(getRoomId(), preparedStmt, getRoomPassword())) {
                            logger.info("Login attempt succeed");
                            login = true;
                        } else {
                            logger.info("Login attempt failed");
                            login = false;
                        }
                        break;
                    }
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return login;
    }

    private boolean comparePasswords(int roomId, PreparedStatement preparedStmt, String pass) throws SQLException {
        logger.info("Creating SELECT");
        String SELECT = "SELECT RoomPassword FROM rooms WHERE RoomID = " + roomId;
        logger.info("SELECT is: " + SELECT);
        ResultSet QUERY = preparedStmt.executeQuery(SELECT);
        while (QUERY.next()) {
            if(QUERY.getString("RoomPassword").equals(pass)) {
                System.out.println(QUERY.getString("RoomPassword"));
                return true;
            }
        }
        return false;
    }
}
