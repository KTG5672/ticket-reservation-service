package io.github.ktg.ticketing.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 에러 API 응답 클래스
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    private final String code;
    private final String message;

}
