package io.github.ktg.ticketing.domain.user.port.out;

public interface TokenProvider {

    String generateAccessToken(String userId);
    String generateRefreshToken(String userId);
    boolean validateAccessToken(String accessToken);
    String getUserIdByAccessToken(String accessToken);

}
