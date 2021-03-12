package database;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.*;

public class GetRoomName extends LoginToMySQL {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private int roomId;

    public GetRoomName(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        if (roomId == 0) return "General";
        logger.info("Start of attempt to login");
        logger.info("Connecting to database");
        String roomName = null;
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT RoomName, RoomID FROM rooms WHERE RoomID = " + roomId;
            logger.info("SELECT is: " + SELECT);
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                logger.info("Querying trough database");
                while (QUERY.next()) {
                    if (QUERY.getInt("RoomID") == roomId) {
                        roomName = QUERY.getString("RoomName");
                        break;
                    }
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return roomName;
    }
}
