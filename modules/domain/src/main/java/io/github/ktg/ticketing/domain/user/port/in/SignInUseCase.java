package io.github.ktg.ticketing.domain.user.port.in;

public interface SignInUseCase {

    SignInResult signIn(SignInCommand command);

}
