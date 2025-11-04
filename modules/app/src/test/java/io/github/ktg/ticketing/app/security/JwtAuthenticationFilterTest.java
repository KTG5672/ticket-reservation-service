package io.github.ktg.ticketing.app.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.ktg.ticketing.domain.user.port.out.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    TokenProvider tokenProvider;
    @Mock
    FilterChain filterChain;

    JwtAuthenticationFilter jwtAuthenticationFilter;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Authentication 헤더가 없으면 미인증 테스트")
    void Authentication_헤더가_없으면_미인증_테스트() throws Exception {
        // given
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효한 토큰 정보면 정상 인증 테스트")
    void 유효한_토큰_정보면_정상_인증_테스트() throws Exception {
        // given
        String token = "token1234";
        String authentication = "Bearer " + token;
        String userId = "userId123";
        request.addHeader("Authorization", authentication);
        when(tokenProvider.validateAccessToken(token)).thenReturn(true);
        when(tokenProvider.getUserIdByAccessToken(token)).thenReturn(userId);
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // then
        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isEqualTo(userId);
    }

    @Test
    @DisplayName("만료된 토큰 정보면 미인증 테스트")
    void 만료된_토큰_정보면_미인증_테스트() throws Exception {
        // given
        String token = "token1234";
        String authentication = "Bearer " + token;
        request.addHeader("Authorization", authentication);
        when(tokenProvider.validateAccessToken(token)).thenThrow(ExpiredJwtException.class);
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}