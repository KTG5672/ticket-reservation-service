package io.github.ktg.ticketing.app.api.exception;

import io.github.ktg.ticketing.common.api.ApiErrorResponse;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * ResponseEntityExceptionHandler 확장 클래스
 * - MVC 제공 ExceptionHandler 재정의
 * - GlobalExceptionHandler 보다 순서를 먼저하여 처리
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MvcExceptionHandler extends ResponseEntityExceptionHandler {

    // @Valid 바디 검증 실패
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @Nonnull HttpHeaders headers,
        @Nonnull HttpStatusCode status,
        @Nonnull WebRequest request) {

        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errorMessage = fieldErrors.stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));

        log.debug("Validation error: {}", errorMessage);
        return toResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // JSON 파싱 실패
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        @Nonnull HttpHeaders headers,
        @Nonnull HttpStatusCode status,
        @Nonnull WebRequest request) {

        String errorMessage = ex.getMessage();
        log.debug("Message not readable: {}", errorMessage);
        return toResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // 잘못된 HTTP 메서드
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        org.springframework.web.HttpRequestMethodNotSupportedException ex,
        @Nonnull HttpHeaders headers,
        @Nonnull HttpStatusCode status,
        @Nonnull WebRequest request) {

        String errorMessage = "Method " + ex.getMethod() + " not supported" +
            (ex.getSupportedHttpMethods() != null && !ex.getSupportedHttpMethods().isEmpty()
                ? ". Supported: " + ex.getSupportedHttpMethods()
                : "");
        log.debug(errorMessage);
        return toResponse(HttpStatus.METHOD_NOT_ALLOWED, errorMessage);
    }

    // 필수 파라미터 누락
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        @Nonnull HttpHeaders headers,
        @Nonnull HttpStatusCode status,
        @Nonnull WebRequest request) {

        String errorMessage = "Missing parameter: " + ex.getParameterName();
        log.debug(errorMessage);
        return toResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // 타입 변환 실패 (쿼리/패스 변수 등)
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex,
        @Nonnull HttpHeaders headers,
        @Nonnull HttpStatusCode status,
        @Nonnull WebRequest request) {

        String errorMessage =
            "Failed to convert parameter '" + ex.getPropertyName() + "' with value '" + ex.getValue() + "'";
        log.debug(errorMessage);
        return toResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    private ResponseEntity<Object> toResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
            .body(ApiErrorResponse.builder()
                .code(status.name())
                .message(message)
                .build());
    }
}