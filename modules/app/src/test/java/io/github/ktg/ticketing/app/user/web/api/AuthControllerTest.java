package io.github.ktg.ticketing.app.user.web.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ktg.ticketing.app.api.exception.ErrorCodeHttpStatusMapper;
import io.github.ktg.ticketing.domain.user.exception.EmailNotValidException;
import io.github.ktg.ticketing.domain.user.exception.UserErrorCode;
import io.github.ktg.ticketing.domain.user.port.in.SignUpCommand;
import io.github.ktg.ticketing.domain.user.port.in.SignUpUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(AuthController.class)
@Import(ErrorCodeHttpStatusMapper.class)
class AuthControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @MockBean
    SignUpUseCase signUpUseCase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @Test
    @DisplayName("회원가입 이메일 필수 입력 테스트 (400 BAD REQUEST)")
    void 회원가입_시_이메일은_필수_입력() throws Exception {
        // given
        String json = "{\"email\":\"\",\"password\":\"test123\"}";
        MockHttpServletRequestBuilder builder = post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message", containsString("이메일은 필수 입력 입니다.")));
    }

    @Test
    @DisplayName("회원가입 패스워드 필수 입력 테스트 (400 BAD REQUEST)")
    void 회원가입_시_패스워드는_필수_입력() throws Exception {
        // given
        String json = "{\"email\":\"test@test.com\",\"password\":\"\"}";
        MockHttpServletRequestBuilder builder = post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message", containsString("패스워드는 필수 입력 입니다.")));
    }

    @Test
    @DisplayName("회원가입 성공 시 유저 식별자 응답 (200 OK)")
    void 회원가입_성공_시_유저_식별자_응답() throws Exception {
        // given
        String expectedUserId = "userId123";
        when(signUpUseCase.signUp(any(SignUpCommand.class))).thenReturn(expectedUserId);
        String json = "{\"email\":\"test@test.com\",\"password\":\"test123\"}";
        MockHttpServletRequestBuilder builder = post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(expectedUserId));
    }

    @Test
    @DisplayName("회원가입 시 이메일 중복 불가 테스트 (409 CONFLICT)")
    void 회원가입_시_이메일_중복시_400_에러() throws Exception {
        // given
        when(signUpUseCase.signUp(any(SignUpCommand.class))).thenThrow(
            new EmailNotValidException(UserErrorCode.EMAIL_DUPLICATED)
        );
        String json = "{\"email\":\"test@test.com\",\"password\":\"test123\"}";
        MockHttpServletRequestBuilder builder = post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("EMAIL_DUPLICATED"))
            .andExpect(jsonPath("$.message").value("중복된 이메일 입니다."));
    }


}