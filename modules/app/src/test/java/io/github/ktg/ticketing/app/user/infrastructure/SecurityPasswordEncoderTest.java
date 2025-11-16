package io.github.ktg.ticketing.app.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ktg.ticketing.domain.user.model.Password;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class SecurityPasswordEncoderTest {

    SecurityPasswordEncoder securityPasswordEncoder;

    @BeforeEach
    void setUp() {
        securityPasswordEncoder = new SecurityPasswordEncoder(new BCryptPasswordEncoder());
    }

    @Test
    @DisplayName("PasswordEncoder.encode 메서드는 Password를 입력 받아 해싱된 값인 PasswordHash를 반환")
    void PasswordEncoder_encode_반환_테스트() {
        // given
        Password password = new Password("test1234");
        // when
        PasswordHash encoded = securityPasswordEncoder.encode(password);
        // then
        assertThat(encoded).isInstanceOf(PasswordHash.class);
        assertThat(password.value()).isNotEqualTo(encoded.value());
    }

    @Test
    @DisplayName("PasswordEncoder.matches 메서드는 Password 와 해싱된 값 PasswordHash 일치 시 true 반환")
    void PasswordEncoder_matches_일치_테스트() {
        // given
        Password password = new Password("test1234");
        PasswordHash encoded = securityPasswordEncoder.encode(password);
        // when
        boolean matches = securityPasswordEncoder.matches(password, encoded);
        // then
        assertThat(matches).isTrue();
    }
    
    @Test
    @DisplayName("PasswordEncoder.matches 메서드는 Password 와 해싱된 값 PasswordHash 불일치 시 false 반환")
    void PasswordEncoder_matches_불일치_테스트() {
        // given
        Password password = new Password("test1234");
        PasswordHash encoded = securityPasswordEncoder.encode(password);
        // when
        boolean matches = securityPasswordEncoder.matches(new Password("test4567"), encoded);
        // then
        assertThat(matches).isFalse();
    }

}