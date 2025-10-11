package io.github.ktg.ticketing.domain.user.port.out;

import io.github.ktg.ticketing.domain.user.model.PasswordHash;

public interface PasswordEncoderPort {

    PasswordHash encode(String password);
    boolean matches(String password, String encodedPassword);

}
