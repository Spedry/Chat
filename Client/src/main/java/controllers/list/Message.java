package controllers.list;

import lombok.Getter;

public class Message {
    @Getter
    final String name;
    @Getter
    final String message;

    public Message(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
