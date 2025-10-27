package io.github.ktg.ticketing.app.user.service;

import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import io.github.ktg.ticketing.domain.user.exception.PasswordNotValidException;
import io.github.ktg.ticketing.domain.user.exception.UserErrorCode;
import io.github.ktg.ticketing.domain.user.model.Password;
import io.github.ktg.ticketing.domain.user.model.User;
import io.github.ktg.ticketing.domain.user.port.in.SignInCommand;
import io.github.ktg.ticketing.domain.user.port.in.SignInResult;
import io.github.ktg.ticketing.domain.user.port.in.SignInUseCase;
import io.github.ktg.ticketing.domain.user.port.out.PasswordEncoderPort;
import io.github.ktg.ticketing.domain.user.port.out.TokenProvider;
import io.github.ktg.ticketing.domain.user.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 로그인 서비스
 * - 로그인 유스케이스 구현 (SignInUseCase)
 */
@Service
@RequiredArgsConstructor
public class SignInService implements SignInUseCase {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    /**
     * 로그인
     * - 존재 하지 않는 이메일 일 때 예외 (EmailNotValidException)
     * - 패스워드 불일치 시 예외 (PasswordNotValidException)
     * - 로그인 검증 후 AccessToken, Refresh(예정) 발급
     * @param command 로그인 정보
     * @return SignInResult(AccessToken, RefreshToken)
     */
    @Override
    public SignInResult signIn(SignInCommand command) {
        String inputEmail = command.email();
        String inputPassword = command.password();
        Password password = new Password(inputPassword);

        User user = userRepository.findByEmail(inputEmail)
            .orElseThrow(() -> new EmailNotValidException(UserErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new PasswordNotValidException(UserErrorCode.PASSWORD_NOT_MATCHED);
        }

        String accessToken = tokenProvider.generateAccessToken(user.getId());
        return new SignInResult(accessToken, null);
    }
}
