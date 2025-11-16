package io.github.ktg.ticketing.app.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ktg.ticketing.app.api.exception.ErrorCodeHttpStatusMapper;
import io.github.ktg.ticketing.common.api.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

    @Mock
    ErrorCodeHttpStatusMapper errorCodeHttpStatusMapper;

    RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        restAuthenticationEntryPoint = new RestAuthenticationEntryPoint(objectMapper, errorCodeHttpStatusMapper);
    }

    @Test
    @DisplayName("미인증 시 UNAUTHORIZED 상태 코드 및 메시지 응답")
    void 미인증_시_UNAUTHORIZED_코드와_메시지_응답() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        BadCredentialsException authException = new BadCredentialsException("JWT Invalid");

        when(errorCodeHttpStatusMapper.get(SecurityErrorCode.UNAUTHORIZED))
            .thenReturn(HttpStatus.UNAUTHORIZED);

        // when
        restAuthenticationEntryPoint.commence(request, response, authException);

        // then
        int status = response.getStatus();
        assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        String body = response.getContentAsString();
        ApiErrorResponse apiErrorResponse = objectMapper.readValue(body, ApiErrorResponse.class);
        assertThat(apiErrorResponse.getCode()).isEqualTo(SecurityErrorCode.UNAUTHORIZED.code());
        assertThat(apiErrorResponse.getMessage()).contains(SecurityErrorCode.UNAUTHORIZED.message());

    }

}