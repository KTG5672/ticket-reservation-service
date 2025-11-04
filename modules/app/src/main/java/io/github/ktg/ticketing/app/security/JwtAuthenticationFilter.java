package io.github.ktg.ticketing.app.security;

import io.github.ktg.ticketing.domain.user.port.out.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 기반 인증 Spring Security Filter
 * - 헤더의 토큰 정보(JWT)를 검증하여 인증 정보를 Security Context 에 등록
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain) throws ServletException, IOException {

        // 1. Header 에서 Authentication 추출
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 2. "Bearer " Prefix 제거
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 토큰 검증
        try {
            if (tokenProvider.validateAccessToken(token)) {
                // 4. 토큰에서 유저 식별자 조회
                String userId = tokenProvider.getUserIdByAccessToken(token);

                // 5. 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of());
                // 6. Security Context에 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // @Todo: RefreshToken 으로 AccessToken 재발급
            log.debug("Expired JWT token : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
