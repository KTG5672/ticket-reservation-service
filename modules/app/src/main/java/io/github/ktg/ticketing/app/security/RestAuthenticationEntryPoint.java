package io.github.ktg.ticketing.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ktg.ticketing.app.api.exception.ErrorCodeHttpStatusMapper;
import io.github.ktg.ticketing.common.api.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ErrorCodeHttpStatusMapper errorCodeMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {

        String message = authException.getMessage();
        HttpStatus httpStatus = errorCodeMapper.get(SecurityErrorCode.UNAUTHORIZED);

        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
            ApiErrorResponse.builder()
                .code(SecurityErrorCode.UNAUTHORIZED.code())
                .message(SecurityErrorCode.UNAUTHORIZED.message() + "(" + message + ")")
                .build()
        ));

    }
}
