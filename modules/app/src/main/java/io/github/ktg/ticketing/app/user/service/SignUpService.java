package io.github.ktg.ticketing.app.user.service;

import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import io.github.ktg.ticketing.domain.user.exception.UserErrorCode;
import io.github.ktg.ticketing.domain.user.model.Email;
import io.github.ktg.ticketing.domain.user.model.Password;
import io.github.ktg.ticketing.domain.user.model.PasswordHash;
import io.github.ktg.ticketing.domain.user.model.User;
import io.github.ktg.ticketing.domain.user.port.in.SignUpCommand;
import io.github.ktg.ticketing.domain.user.port.in.SignUpUseCase;
import io.github.ktg.ticketing.domain.user.port.out.PasswordEncoderPort;
import io.github.ktg.ticketing.domain.user.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입 서비스
 * - 회원가입 유스케이스 구현
 */
@Service
@RequiredArgsConstructor
public class SignUpService implements SignUpUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    /**
     * 회원가입
     * - 중복 이메일(trim + lowercase) 가입 불가
     * - 패스워드는 인코딩(해시) 처리 후 저장
     * - 회원정보 DB 저장
     * @param signUpCommand 회원가입 정보
     * @return String 유저 식별자
     */
    @Transactional
    @Override
    public String signUp(SignUpCommand signUpCommand) {
        String inputPassword = signUpCommand.password();
        String inputEmail = signUpCommand.email();

        // 이메일 정규화 후 검증
        Email email = new Email(inputEmail);
        validateDuplicatedEmail(email.value());

        Password password = new Password(inputPassword);
        PasswordHash encodedPassword = passwordEncoder.encode(password);
        User user = User.withoutId(email, encodedPassword);

        User saved = userRepository.save(user);
        return saved.getId();
    }

    private void validateDuplicatedEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new EmailNotValidException(UserErrorCode.EMAIL_DUPLICATED);
        });
    }
}
