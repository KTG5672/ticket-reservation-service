package io.github.ktg.ticketing.domain.user.port.out;

import io.github.ktg.ticketing.domain.user.model.Password;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;

public interface PasswordEncoderPort {

    PasswordHash encode(Password password);
    boolean matches(String password, String encodedPassword);

}
