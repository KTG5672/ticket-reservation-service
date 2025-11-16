package io.github.ktg.ticketing.app.user.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.ticketing.domain.user.port.out.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    @DisplayName("유저 식별자로 Access Token 생성 테스트")
    void 유저_식별자로_access_token_생성_테스트() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "userId12345";
        // when
        String accessToken = tokenProvider.generateAccessToken(userId);
        // then
        assertThat(accessToken).isNotEmpty();
    }

    @Test
    @DisplayName("Access Token 검증 성공 테스트")
    void 생성한_access_token_검증_테스트() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "userId12345";
        String accessToken = tokenProvider.generateAccessToken(userId);
        // when
        boolean isValid = tokenProvider.validateAccessToken(accessToken);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Access Token 검증 실패 테스트")
    void 임의의_access_token_검증_실패_테스트() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String accessToken = "asdasdgqgqwg!!!";
        // when
        boolean isValid = tokenProvider.validateAccessToken(accessToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 Access Token 테스트")
    void 만료된_access_token_테스트() throws Exception {
        // given
        TokenProvider tokenProvider = getTokenProvider(1L);
        String userId = "userId12345";
        String accessToken = tokenProvider.generateAccessToken(userId);
        Thread.sleep(5);
        // when
        // then
        assertThatThrownBy(() -> tokenProvider.validateAccessToken(accessToken))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Access Token을 이용하여 유저 식별자 조회 테스트")
    void access_token을_이용하여_유저_식별자_조회_테스트() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "userId12345";
        String accessToken = tokenProvider.generateAccessToken(userId);
        // when
        String userIdByAccessToken = tokenProvider.getUserIdByAccessToken(accessToken);

        // then
        assertThat(userIdByAccessToken).isNotEmpty();
        assertThat(userIdByAccessToken).isEqualTo(userId);
    }

    TokenProvider getTokenProvider(Long expiredMillis) {
        return new JwtTokenProvider("application", "dGlja2V0aW5nLXNlcnZpY2UtYXV0aGVudGljYXRpb24=",
            expiredMillis, 0L);
    }

}