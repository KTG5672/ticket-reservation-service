package io.github.ktg.ticketing.common.security;

/**
 * 현재 로그인된 사용자 식별자 제공 인터페이스
 * - 반환 값은 null이 아니어야함
 */
public interface CurrentUserProvider {
    String getCurrentUserId();
}
