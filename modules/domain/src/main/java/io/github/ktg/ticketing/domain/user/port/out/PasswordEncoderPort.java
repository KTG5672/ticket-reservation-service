package io.github.ktg.ticketing.domain.user.port.out;

public interface PasswordEncoderPort {

    String encode(String password);
    boolean matches(String password, String encodedPassword);

}
