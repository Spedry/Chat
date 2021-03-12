package database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.Room;

import java.sql.*;
import java.util.ArrayList;

public class LoadRooms extends LoginToMySQL {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private ArrayList<Room> lisfOfRooms;

    public LoadRooms() {
        lisfOfRooms = new ArrayList<>();
    }

    public ArrayList<Room> createListofRooms() {
        logger.info("Start of attempt to login");
        logger.info("Connecting to database");
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            String SELECT = "SELECT RoomID FROM rooms";
            logger.info("SELECT is: " + SELECT);
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                logger.info("Querying trough database");
                while (QUERY.next()) {
                    lisfOfRooms.add(new Room(QUERY.getInt("RoomID")));
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return lisfOfRooms;
    }
}
