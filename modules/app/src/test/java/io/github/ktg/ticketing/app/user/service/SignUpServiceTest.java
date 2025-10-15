package io.github.ktg.ticketing.app.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import io.github.ktg.ticketing.domain.user.model.Email;
import io.github.ktg.ticketing.domain.user.model.Password;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;
import io.github.ktg.ticketing.domain.user.model.User;
import io.github.ktg.ticketing.domain.user.port.in.SignUpCommand;
import io.github.ktg.ticketing.domain.user.port.out.PasswordEncoderPort;
import io.github.ktg.ticketing.domain.user.port.out.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {
    
    SignUpService signUpService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setUp() {
        signUpService = new SignUpService(userRepository, passwordEncoder);
    }


    @Test
    @DisplayName("회원가입 시 패스워드는 인코딩 하여 DB 저장된다.")
    void 회원가입시_패스워드는_인코딩하여_저장() {
        // given
        String inputEmail = "test@test.com";
        String inputPassword = "password123";

        Email email = new Email(inputEmail);
        Password password = new Password(inputPassword);
        PasswordHash encodedPassword = new PasswordHash("encodedPassword123");
        SignUpCommand signUpCommand = new SignUpCommand(inputEmail, inputPassword);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(User.withoutId(email, encodedPassword));

        // when
        signUpService.signUp(signUpCommand);

        // then
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(email);
        assertThat(captor.getValue().getPassword().value()).isEqualTo(encodedPassword.value());
    }

    @Test
    @DisplayName("회원가입 시 유저 정보가 DB에 저장된다.")
    void 회원가입시_정보가_DB에_저장() {
        // given
        String inputEmail = "email@test.com";
        String inputPassword = "password123";

        Email email = new Email(inputEmail);
        Password password = new Password(inputPassword);
        PasswordHash encodedPassword = new PasswordHash("encodedPassword123");
        SignUpCommand signUpCommand = new SignUpCommand(inputEmail, inputPassword);
        when(userRepository.save(any(User.class))).thenReturn(User.withoutId(email, encodedPassword));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // when
        signUpService.signUp(signUpCommand);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 시 중복 이메일은 불가능 하다.")
    void 회원가입시_중복_이메일_불가() {
        // given
        String inputEmail = "email@test.com";
        String inputPassword = "password123";

        Email email = new Email(inputEmail);
        PasswordHash encodedPassword = new PasswordHash("encodedPassword123");
        SignUpCommand signUpCommand = new SignUpCommand(inputEmail, inputPassword);
        User dupUser = User.withoutId(email, encodedPassword);
        when(userRepository.findByEmail(inputEmail)).thenReturn(Optional.of(dupUser));

        // when
        // then
        assertThatThrownBy(() -> signUpService.signUp(signUpCommand))
            .isInstanceOf(EmailNotValidException.class);
    }

    @Test
    @DisplayName("회원가입 후 유저 식별자를 리턴")
    void 회원가입_후_유저_식별자를_리턴() {
        // given
        String inputEmail = "email@test.com";
        String inputPassword = "password123";
        String expectedUserId = "userId123";

        Email email = new Email(inputEmail);
        Password password = new Password(inputPassword);
        PasswordHash encodedPassword = new PasswordHash("encodedPassword123");
        SignUpCommand signUpCommand = new SignUpCommand(inputEmail, inputPassword);
        User mockUser = User.withoutId(email, encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser.withId(expectedUserId));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // when
        String result = signUpService.signUp(signUpCommand);

        // then
        assertThat(result).isEqualTo(expectedUserId);
    }


    
}