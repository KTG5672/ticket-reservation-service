package io.github.ktg.ticketing.domain.user.model;

import lombok.Getter;

@Getter
public class User {

    private String id;
    private final String email;
    private final Password password;

    public User(String email, Password password) {
        this.email = email;
        this.password = password;
    }
}
