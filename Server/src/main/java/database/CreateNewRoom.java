package database;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.*;

public class CreateNewRoom extends AbsRoom {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public CreateNewRoom(@NonNull JSONObject jsonObject) {
        super(jsonObject);
        logger.info("CreateNewRooms class was created");
    }

    public boolean createRoom() {
        logger.info("Start of attempt to create room");
        logger.info("Connecting to database");
        boolean createdRoom = false;
        try (Connection conn = DriverManager.getConnection(getJdbcURL(), getUser(), getPassword())) {
            logger.info("Successfully connected to database");
            logger.info("Creating SELECT");
            String SELECT = "SELECT RoomName FROM rooms";
            logger.debug("SELECT is: " + SELECT);
            boolean possibleToCreate = true;
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                ResultSet QUERY = preparedStmt.executeQuery(SELECT);
                logger.info("Querying trough database");
                while(QUERY.next()) {
                    String UserName = QUERY.getString("RoomName");
                    if (UserName.equals(getRoomName())) {
                        possibleToCreate = false;
                        logger.info("The inserted room name is already in use");
                        break;
                    }
                }
            }
            logger.info("Creating SELECT");
            SELECT = "INSERT INTO rooms (RoomName, RoomPassword) VALUES (?, ?)";
            if (possibleToCreate) {
                try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                    logger.debug("SELECT is: " + SELECT);
                    logger.info(getRoomName() + getRoomPassword());
                    preparedStmt.setString(1, getRoomName());
                    preparedStmt.setString(2, getRoomPassword());
                    preparedStmt.execute();
                    createdRoom = true;
                    logger.info("New user successfully registered");
                }
            }
            SELECT = "SELECT RoomID, RoomName FROM rooms WHERE RoomName = ?";
            try (PreparedStatement preparedStmt = conn.prepareStatement(SELECT)) {
                logger.debug("SELECT is: " + SELECT);
                preparedStmt.setString(1, getRoomName());
                ResultSet QUERY = preparedStmt.executeQuery();
                while (QUERY.next()) {
                    if(QUERY.getString("RoomName").equals(getRoomName())) {
                        setRoomId(QUERY.getInt("RoomID"));
                        break;
                    }
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error trying connect to database", sqle);
        }
        return createdRoom;
    }
}
