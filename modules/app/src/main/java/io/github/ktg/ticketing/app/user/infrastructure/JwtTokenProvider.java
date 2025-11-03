package io.github.ktg.ticketing.app.user.infrastructure;

import io.github.ktg.ticketing.domain.user.port.out.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * TokenProvider 구현체
 * - io.jsonwebtoken 기반으로 구현
 * - Access Token 생성/검증/Subject 조회 기능
 */
@Log4j2
@Component
public class JwtTokenProvider implements TokenProvider {

    private final String issuer;
    private final Long expireMilliseconds;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
        @Value("${spring.application.name}") String issuer,
        @Value("${jwt.secret-key}") String secretKey,
        @Value("${jwt.expire-milliseconds}") Long expireMilliseconds,
        @Value("${jwt.clock-skew-seconds:30}") Long clockSkewSeconds) {
        this.issuer = issuer;
        this.expireMilliseconds = expireMilliseconds;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        jwtParser = Jwts.parser()
            .requireIssuer(issuer)
            .clockSkewSeconds(clockSkewSeconds)
            .verifyWith(this.secretKey)
            .build();
    }

    /**
     * Access Token 생성
     * - expireMilliseconds 만큼 유효한 Access Token 생성
     * @param userId 유저 식별자
     * @return String AccessToken
     */
    @Override
    public String generateAccessToken(String userId) {
        Instant now = Instant.now();
        Instant expired = now.plusMillis(expireMilliseconds);

        return Jwts.builder()
            .subject(userId)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expired))
            .signWith(secretKey)
            .compact();
    }

    // @Todo
    @Override
    public String generateRefreshToken(String userId) {
        return "";
    }

    /**
     * Access Token 유효성 검증
     * @param accessToken AccessToken
     * @return boolean 일치 여부
     * @throws ExpiredJwtException 만료된 토큰인 경우
     */
    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            jwtParser.parseSignedClaims(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getClass().getSimpleName());
        }
        return false;
    }

    /**
     * Access Token 에서 유저 식별자(Subject) 조회
     * @param accessToken AccessToken
     * @return String 유저 식별자
     */
    @Override
    public String getUserIdByAccessToken(String accessToken) {
        Jws<Claims> claimsJws = jwtParser.parseSignedClaims(accessToken);
        Claims payload = claimsJws.getPayload();
        return payload.getSubject();
    }
}
