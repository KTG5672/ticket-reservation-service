package io.github.ktg.ticketing.app.api.exception;

import static io.github.ktg.ticketing.app.api.exception.GlobalExceptionTestController.TestErrorCode.TEST_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ktg.ticketing.common.api.ApiErrorResponse;
import io.github.ktg.ticketing.common.exception.CommonErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest(GlobalExceptionTestController.class)
@Import(ErrorCodeHttpStatusMapper.class)
class GlobalExceptionHandlerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    /**
     * Exception Handler 테스트
     * - 정의 되지 않거나 비지니스 예외가 아닌 예외는 모두 UNKNOWN_ERROR (500) 응답
     */
    @Test
    @DisplayName("Exception 핸들러 테스트")
    void exception_handler_테스트() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/exception");
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .code(CommonErrorCode.UNKNOWN_ERROR.code())
            .message(CommonErrorCode.UNKNOWN_ERROR.message())
            .build();
        String expectBody = objectMapper.writeValueAsString(errorResponse);
        perform.andExpect(status().isInternalServerError())
            .andExpect(content().string(expectBody));
    }

    /**
     * Business Exception Handler 테스트
     * - TEST_ERROR 는 ErrorCodeHttpStatusMapper에 정의되지 않은 코드라 BadRequest 응답
     */
    @Test
    @DisplayName("Business Exception 핸들러 테스트")
    void business_exception_handler_테스트() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/business-exception");
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .code(TEST_ERROR.code())
            .message(TEST_ERROR.message())
            .build();
        String expectBody = objectMapper.writeValueAsString(errorResponse);
        perform.andExpect(status().isBadRequest())
            .andExpect(content().string(expectBody));
    }

}