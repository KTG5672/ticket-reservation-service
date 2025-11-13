package io.github.ktg.ticketing.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 에러 API 응답 클래스
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {

    private String code;
    private String message;

}
