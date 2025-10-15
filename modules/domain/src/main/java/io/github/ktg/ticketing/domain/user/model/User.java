package io.github.ktg.ticketing.domain.user.model;

import lombok.Getter;

@Getter
public class User {

    private final String id;
    private final Email email;
    private final PasswordHash password;

    private User(String id, Email email, PasswordHash password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public static User withoutId(Email email, PasswordHash password) {
        return new User(null, email, password);
    }

    public User withId(String id) {
        return new User(id, email, password);
    }

}
