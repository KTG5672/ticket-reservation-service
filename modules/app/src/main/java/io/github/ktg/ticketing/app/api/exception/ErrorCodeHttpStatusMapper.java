package io.github.ktg.ticketing.app.api.exception;

import io.github.ktg.ticketing.common.exception.CommonErrorCode;
import io.github.ktg.ticketing.common.exception.ErrorCode;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ErrorCode -> HttpStatus 매핑 클래스
 * - MAPPING 에 정의된 ErrorCode 클래스 -> HttpStatus 변환하는 기능 제공
 */
@Component
public class ErrorCodeHttpStatusMapper {

    private static final Map<Class<? extends ErrorCode>, HttpStatus> MAPPING = Map.of(
        CommonErrorCode.class, HttpStatus.INTERNAL_SERVER_ERROR
    );

    /**
     * ErrorCode 를 받아 매핑된 HttpStatus 리턴
     * - 미매핑된 ErrorCode는 BadRequest(400)으로 간주
     * @param errorCode ErrorCode
     * @return HttpStatus
     */
    public HttpStatus get(ErrorCode errorCode) {
        return MAPPING.getOrDefault(errorCode.getClass(), HttpStatus.BAD_REQUEST);
    }

}
