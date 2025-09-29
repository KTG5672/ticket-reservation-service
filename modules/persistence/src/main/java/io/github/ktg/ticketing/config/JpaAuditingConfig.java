package io.github.ktg.ticketing.config;

import io.github.ktg.ticketing.common.security.CurrentUserProvider;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Jpa Auditing Config 클래스
 * - Auditing Auto Config
 * - CurrentUserProvider 이용한 작성/수정자 기능 제공
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    /**
     * 현재 사용자의 식별자를 @CreatedBy, @LastModifiedBy 필드에 제공
     * @param currentUserProvider 현재 사용자 정보 제공 인터페이스
     * @return AuditorAware<String>
     */
    @Bean
    public AuditorAware<String> auditorAware(CurrentUserProvider currentUserProvider) {
        return () -> Optional.of(currentUserProvider.getCurrentUserId());
    }

    /**
     * CurrentUserProvider 구현체가 없을 때 Default Bean 제공
     * @return CurrentUserProvider
     */
    @Bean
    @ConditionalOnMissingBean(CurrentUserProvider.class)
    public CurrentUserProvider defaultCurrentUserProvider() {
        return () -> "system";
    }

}
