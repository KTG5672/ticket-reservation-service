package io.github.ktg.ticketing.domain.user.port.in;

public record SignInResult(String accessToken, String refreshToken) {
}
