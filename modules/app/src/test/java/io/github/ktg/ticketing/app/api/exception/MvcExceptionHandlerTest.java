package io.github.ktg.ticketing.app.api.exception;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.ktg.ticketing.app.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(value = MvcExceptionTestController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@Import(ErrorCodeHttpStatusMapper.class)
class MvcExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("handleMethodArgumentNotValid 테스트")
    void valid_바디_파라미터_검증_실패_핸들러() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = post("/argument-valid")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"message\":\"\"}");
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message", containsString("message is not blank")));
    }

    @Test
    @DisplayName("handleHttpMessageNotReadable 테스트")
    void JSON_파싱_실패_핸들러() throws Exception {
        // given
        String json = "{\"code\":\"BAD_REQUEST\",\"message\":\"Bad Request\""; // 마지막 중괄호 누락
        MockHttpServletRequestBuilder builder = post("/argument-valid")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message", containsString("JSON parse error:")));
    }

    @Test
    @DisplayName("handleHttpRequestMethodNotSupported 테스트")
    void 잘못된_HTTP_메서드_요청_핸들러() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/argument-valid");
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"))
            .andExpect(jsonPath("$.message", containsString("Method GET not supported")));
    }

    @Test
    @DisplayName("handleMissingServletRequestParameter 테스트")
    void 필수_파라미터_누락_핸들러() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/request-parameter");
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message", containsString("Missing parameter: param")));
    }

    @Test
    @DisplayName("handleNoHandlerFoundException 테스트")
    void 쿼리_패스_변수_타입_변환_실패() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = get("/request-parameter?param=value");
        // when
        ResultActions perform = mockMvc.perform(builder);
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.message", containsString("Failed to convert parameter 'param' with value 'value'")));
    }





}