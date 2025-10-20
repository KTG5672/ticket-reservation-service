package io.github.ktg.ticketing.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Open API Config 클래스
 * - API 인증 방법 명세 (JWT)
 * - 도메인 별 그룹 정의
 */
@OpenAPIDefinition(
    info = @Info(title = "Ticket Reservation Service API", version = "v1"),
    security = {@SecurityRequirement(name = "bearerAuth")}
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(
            new Components().addSecuritySchemes(
                "bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
        );
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
            .group("user")
            .packagesToScan("io.github.ktg.ticketing.app.user")
            .build();
    }

    @Bean
    public GroupedOpenApi reservationApi() {
        return GroupedOpenApi.builder()
            .group("reservation")
            .packagesToScan("io.github.ktg.ticketing.app.reservation")
            .build();
    }

    @Bean
    public GroupedOpenApi eventApi() {
        return GroupedOpenApi.builder()
            .group("event")
            .packagesToScan("io.github.ktg.ticketing.app.event")
            .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
            .group("payment")
            .packagesToScan("io.github.ktg.ticketing.app.payment")
            .build();
    }
}
