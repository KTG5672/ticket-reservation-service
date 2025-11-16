package io.github.ktg.ticketing.app.user.infrastructure;

import io.github.ktg.ticketing.domain.user.model.Password;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;
import io.github.ktg.ticketing.domain.user.port.out.PasswordEncoderPort;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * PasswordEncoderPort 구현체
 * - Spring Security 에서 제공하는 PasswordEncoder 의존, 어댑팅
 */
@Component
@RequiredArgsConstructor
public class SecurityPasswordEncoder implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    /**
     * 패스워드 인코딩 메서드
     * @param password Password 값 객체
     * @return PasswordHash 해싱된 패스워드 값 객체
     */
    @Override
    public PasswordHash encode(@NotNull Password password) {
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        String encoded = passwordEncoder.encode(password.value());
        return new PasswordHash(encoded);
    }

    /**
     * 패스워드와 해싱 패스워드 일치 여부 메서드
     * @param password Password 값 객체
     * @param encodedPassword PasswordHash 해싱된 패스워드 값 객체
     * @return Boolean 일치 여부
     */
    @Override
    public boolean matches(@NotNull Password password, @NotNull PasswordHash encodedPassword) {
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        if (encodedPassword == null) {
            throw new IllegalArgumentException("EncodedPassword must not be null");
        }
        return passwordEncoder.matches(password.value(), encodedPassword.value());
    }
}
