package io.github.ktg.ticketing.app.api.exception;

import io.github.ktg.ticketing.common.exception.CommonErrorCode;
import io.github.ktg.ticketing.common.exception.ErrorCode;
import io.github.ktg.ticketing.domain.user.exception.UserErrorCode;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ErrorCode -> HttpStatus 매핑 클래스
 * - CLASS_DEFAULTS, CODE_DETAILS 에 정의된 ErrorCode 클래스 -> HttpStatus 변환하는 기능 제공
 */
@Component
public class ErrorCodeHttpStatusMapper {

    private static final Map<Class<? extends ErrorCode>, HttpStatus> CLASS_DEFAULTS = Map.of(
        CommonErrorCode.class, HttpStatus.INTERNAL_SERVER_ERROR,
        UserErrorCode.class, HttpStatus.BAD_REQUEST
    );

    private static final Map<ErrorCode, HttpStatus> CODE_DETAILS = Map.of(
        UserErrorCode.EMAIL_DUPLICATED, HttpStatus.CONFLICT
    );

    /**
     * ErrorCode 를 받아 매핑된 HttpStatus 리턴
     * - ErrorCode 객체 별 매핑 HttpStatus -> ErrorCode 클래스 매핑 HttpStatus 순으로 매핑
     * - 미매핑된 ErrorCode는 BadRequest(400)으로 간주
     * @param errorCode ErrorCode
     * @return HttpStatus
     */
    public HttpStatus get(ErrorCode errorCode) {
        if (CODE_DETAILS.containsKey(errorCode)) {
            return CODE_DETAILS.get(errorCode);
        }
        return CLASS_DEFAULTS.getOrDefault(errorCode.getClass(), HttpStatus.BAD_REQUEST);
    }

}
