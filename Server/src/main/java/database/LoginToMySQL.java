package database;

import lombok.Getter;

public abstract class LoginToMySQL {
    @Getter
    private final String jdbcURL = "jdbc:mysql://localhost:3306/chat?useSSL=false&allowPublicKeyRetrieval=true",
            user = "root",
            password = "admin";
}
