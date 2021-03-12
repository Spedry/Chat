package controllers.list;

import lombok.Getter;

public class Room {
    @Getter
    final String name;
    @Getter
    final int ID;

    public Room(int ID, String name) {
        this.name = name;
        this.ID = ID;
    }
}