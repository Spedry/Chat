package controllers.list;

import lombok.Getter;

public class Room {
    @Getter
    final String name;
    @Getter
    final int ID;

    public Room(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }
}