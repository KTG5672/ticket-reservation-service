package io.github.ktg.ticketing.app.api.exception;

import io.github.ktg.ticketing.common.api.ApiErrorResponse;
import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.CommonErrorCode;
import io.github.ktg.ticketing.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global 예외 핸들러
 * - BusinessException, Exception 공통 에러 처리 제공
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorCodeHttpStatusMapper errorCodeHttpStatusMapper;

    /**
     * 비지니스 예외 처리
     * - BusinessException 내의 ErrorCode 사용하여 Code, Message 응답
     * - ErrorCodeHttpStatusMapper 사용하여 정의된 ErrorCode -> HttpStatus 변환하여 응답
     * @param e BusinessException
     * @return ResponseEntity<ApiErrorResponse>
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus = errorCodeHttpStatusMapper.get(errorCode);
        return ResponseEntity.status(httpStatus).body(
            ApiErrorResponse.builder()
                .code(errorCode.code())
                .message(errorCode.message())
                .build()
        );
    }

    /**
     * Exception 예외 처리
     * - 위 모든 예외 처리가 실패한 경우 UNKNOWN_ERROR (500) 응답
     * @param e Exception
     * @return ResponseEntity<ApiErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(
            ApiErrorResponse.builder()
                .code(CommonErrorCode.UNKNOWN_ERROR.code())
                .message(CommonErrorCode.UNKNOWN_ERROR.message())
                .build()
        );
    }

}
