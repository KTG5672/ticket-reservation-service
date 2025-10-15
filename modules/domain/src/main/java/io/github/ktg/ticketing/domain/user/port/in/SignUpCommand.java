package io.github.ktg.ticketing.domain.user.port.in;

public record SignUpCommand(String email, String password) {
}
