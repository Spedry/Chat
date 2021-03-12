package server;

import database.LoadRooms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import shortcuts_for_M_and_V.Methods;
import shortcuts_for_M_and_V.Variables;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final ClientHadnlerList clientHandlerList;
    private final LinkedBlockingQueue<JSONObject> messages;
    private final ArrayList<Room> rooms;

    public MessageHandler() {
        clientHandlerList = new ClientHadnlerList();
        messages = new LinkedBlockingQueue<>();
        rooms = new ArrayList<>();
        rooms.add(new Room(0));
    }

    // Load existing rooms in Database
    public void loadRoom() {
        LoadRooms loadRooms = new LoadRooms();
        for (Room room : loadRooms.createListofRooms()) {
            rooms.add(room);
        }
    }

    // Crete new room
    public void createRoom(int roomID) {
        logger.info("Creating new room " + roomID);
        rooms.add(new Room(roomID));
        logger.info("Number of rooms is: " + rooms.size());
    }

    // Delete an existing room
    public void deleteRoom(int roomID) {
        for (Room room : rooms) {
            if (room.getRoomID() == roomID) {
                for (ClientHandler clientHandler : room.getClientsInRoom().getList()) {
                    addClientIntotheRoom(0, clientHandler);
                }
                rooms.remove(room);
                break;
            }
        }
    }

    // Add a client into clientHandlerList
    public void addToClientList(ClientHandler clientHandler) {
        clientHandlerList.add(clientHandler);
        logger.info("Adding client into room 0");
        rooms.get(0).addClient(clientHandler);
        sendMessageHistory(clientHandler, rooms.get(0).getMessageHistory());
        //sendOnlineUser(clientHandler, Variables.SHOW_ONLINE_USER);
        logger.info("Thread was add into the arraylist");
        logger.info("Number of clients in list after ADD: " + clientHandlerList.size());
    }

    // Delete a client from clientHandlerList
    public void deleteFromClientList(ClientHandler clientHandler) {
        clientHandlerList.remove(clientHandler);
        deleteClientFromRoom(clientHandler);
        //sendOnlineUser(clientHandler, Variables.DELETE_ONLINE_USER);
        logger.info("Thread was removed from arraylist");
        logger.info("Number of clients in list after REMOVE: " + clientHandlerList.size());
    }

    // Add a client into the room
    public void addClientIntotheRoom(int roomId, ClientHandler clientHandler) {
        logger.info("Adding client into the room " + roomId);
        for (Room room : rooms) {
            if (room.getRoomID() == roomId) {
                room.addClient(clientHandler);
                logger.info("Clients in room" + roomId + ": " + room.getClientsInRoom().getList().size());
                sendMessageHistory(clientHandler, room.getMessageHistory());
                break;
            }
        }
    }

    // Delete a client from room and move him to room0
    public void deleteClientFromRoom(ClientHandler clientHandler) {
        for (Room room : rooms) {
            if (room.getRoomID() == clientHandler.getThisThreadRoomID()) {
                logger.info("Deleting clients from room " + room.getRoomID());
                room.removeClient(clientHandler);
                sendOnlineUser(clientHandler, Variables.DELETE_ONLINE_USER, room.getClientsInRoom());
                break;
            }
        }
    }

    //
    public void sendMessageHistory(ClientHandler clientHandler, LinkedBlockingQueue<JSONObject> messageHistory) {
        if (!messageHistory.isEmpty())
            for (JSONObject jsonObject : messageHistory) {
                clientHandler.cast(jsonObject);
            }
    }

    public void addtoRoomMessageHistory(int roomId, JSONObject message) throws InterruptedException {
        for (Room room : rooms) {
            if (room.getRoomID() == roomId) {
                room.addMessagetoHistory(message);
            }
        }
    }

    public void addToMessages(JSONObject message) throws InterruptedException {
        messages.put(message);
        addtoRoomMessageHistory(message.getJSONObject(Variables.DATA).getInt(Variables.ROOM_ID), message);
        logger.info("Message was add into queue");
    }

    public void sendOnlineUsersList(ClientHandler clientHandler) {
        List<String> listofOnlineUsers = new ArrayList<>();
        for(ClientHandler clientInList : rooms.get(0).getClientsInRoom().getList()) {
            if (clientInList.equals(clientHandler)) continue;
            else {
                clientInList.cast(Methods.createJson(Variables.SHOW_ONLINE_USER, Variables.USERNAME, clientHandler.getThisThreadUserName()));
                listofOnlineUsers.add(clientInList.getThisThreadUserName());
            }
            break;
        }
        clientHandler.cast(Methods.createJsonListofUsers(listofOnlineUsers));
    }

    public void sendOnlineUser(ClientHandler clientHandler, String ID, ClientHadnlerList clientHadnlerList) {
        for (ClientHandler clientInList : clientHadnlerList.getList()) {
            if (clientInList.equals(clientHandler)) continue;
            else clientInList.cast(Methods.createJson(ID, Variables.USERNAME, clientHandler.getThisThreadUserName()));
        }
    }

    public void sendRoomList(int roomId, ClientHandler clientHandler) {
        List<String> listofOnlineUsers = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getRoomID() == roomId) {
                for (ClientHandler clientInList : room.getClientsInRoom().getList()) {
                    if (clientInList.equals(clientHandler)) continue;
                    else {
                        logger.info("Sending new user: " + clientHandler.getThisThreadUserName() + " to users in room" + roomId);
                        clientInList.cast(Methods.createJson(Variables.SHOW_ONLINE_USER, Variables.USERNAME, clientHandler.getThisThreadUserName(), "LOL", "LOL"));
                        logger.info("Adding user into list");
                        listofOnlineUsers.add(clientInList.getThisThreadUserName());
                    }
                }
                logger.info("Sending list of users in room to new logged user");
                clientHandler.cast(Methods.createJsonListofUsers(listofOnlineUsers));
                break;
            }
        }

    }

    void cast() throws InterruptedException {
        JSONObject message = messages.take();
        logger.info("MSG:" + message);
        for (Room room : rooms) {
            logger.info(room.getRoomID());
            int roomID = message.getJSONObject(Variables.DATA).getInt(Variables.ROOM_ID);
            if (room.getRoomID() == roomID || roomID == 0) {
                logger.info("MATCH");
                for (ClientHandler client : room.getClientsInRoom().getList()) {
                    logger.info("Sending msg");
                    client.cast(message);
                }
                break;
            }
        }
    }
}