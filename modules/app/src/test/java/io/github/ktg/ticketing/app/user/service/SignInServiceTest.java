package io.github.ktg.ticketing.app.user.service;

import static io.github.ktg.ticketing.domain.user.exception.UserErrorCode.EMAIL_NOT_FOUND;
import static io.github.ktg.ticketing.domain.user.exception.UserErrorCode.PASSWORD_NOT_MATCHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import io.github.ktg.ticketing.domain.user.exception.PasswordNotValidException;
import io.github.ktg.ticketing.domain.user.model.Email;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;
import io.github.ktg.ticketing.domain.user.model.User;
import io.github.ktg.ticketing.domain.user.port.in.SignInCommand;
import io.github.ktg.ticketing.domain.user.port.in.SignInResult;
import io.github.ktg.ticketing.domain.user.port.out.PasswordEncoderPort;
import io.github.ktg.ticketing.domain.user.port.out.TokenProvider;
import io.github.ktg.ticketing.domain.user.port.out.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignInServiceTest {

    SignInService signInService;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoderPort passwordEncoderPort;

    @BeforeEach
    void setUp() {
        signInService = new SignInService(tokenProvider, userRepository, passwordEncoderPort);
    }

    @Test
    @DisplayName("로그인 성공 시 액세스 토큰 발급")
    void 로그인_시_액세스_토큰을_발급() {
        // given
        String email = "test@test.com";
        String password = "test123";
        String expectedUserId = "userId123";
        User expectedUser = User.withoutId(new Email(email), new PasswordHash(password))
            .withId(expectedUserId);
        SignInCommand signInCommand = new SignInCommand(email, password);
        when(tokenProvider.generateAccessToken(expectedUserId)).thenReturn("access_token");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));
        when(passwordEncoderPort.matches(password, expectedUser.getPassword().value())).thenReturn(true);
        // when
        SignInResult signInResult = signInService.signIn(signInCommand);

        // then
        assertThat(signInResult.accessToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인 시 존재하지 않은 이메일 입력 시 에러")
    void 로그인_시_존재하지_않는_이메일_입력_시_에러() {
        // given
        String email = "test@test.com";
        String password = "test123";
        SignInCommand signInCommand = new SignInCommand(email, password);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> signInService.signIn(signInCommand))
            .isInstanceOf(EmailNotValidException.class)
            .hasMessageContaining(EMAIL_NOT_FOUND.message());
    }

    @Test
    @DisplayName("로그인 시 패스워드 불일치 시 에러")
    void 로그인_시_패스워드_불일치_시_에러() {
        // given
        String email = "test@test.com";
        String password = "test123";
        String expectedUserId = "userId123";
        User expectedUser = User.withoutId(new Email(email), new PasswordHash(password))
            .withId(expectedUserId);
        SignInCommand signInCommand = new SignInCommand(email, password);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));
        when(passwordEncoderPort.matches(password, expectedUser.getPassword().value())).thenReturn(false);
        // when
        // then
        assertThatThrownBy(() -> signInService.signIn(signInCommand))
            .isInstanceOf(PasswordNotValidException.class)
            .hasMessageContaining(PASSWORD_NOT_MATCHED.message());
    }

}