package io.github.ktg.ticketing.domain.user.model;

import lombok.Getter;

@Getter
public class User {

    private String id;
    private final String email;
    private final PasswordHash password;

    public User(String email, PasswordHash password) {
        this.email = email;
        this.password = password;
    }

}
