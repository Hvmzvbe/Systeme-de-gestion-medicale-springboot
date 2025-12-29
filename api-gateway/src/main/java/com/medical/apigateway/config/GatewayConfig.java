package com.medical.apigateway.config;

import com.medical.apigateway.filter.JwtAuthenticationFilter;
import com.medical.apigateway.filter.LoggingFilter;
import com.medical.apigateway.filter.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayConfig  {

    private final LoggingFilter loggingFilter;
    private final RateLimitingFilter rateLimitingFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(
            LoggingFilter loggingFilter,
            RateLimitingFilter rateLimitingFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.loggingFilter = loggingFilter;
        this.rateLimitingFilter = rateLimitingFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()

                // ===== MS PATIENTS =====
                .route("ms-patients", r -> r
                        .path("/api/patients/**", "/api/dossiers/**", "/api/health")
                        .filters(f -> f
                                .filter( loggingFilter)
                                .filter(rateLimitingFilter)
                                .filter(jwtAuthenticationFilter)
                        )
                        .uri("lb://ms-patients")
                )

                // ===== APPOINTMENTS SERVICE =====
                .route("appointments-service", r -> r
                        .path("/api/appointments/**", "/api/doctors/**")
                        .filters(f -> f
                                .filter( loggingFilter)
                                .filter(rateLimitingFilter)
                                .filter( jwtAuthenticationFilter)
                        )
                        .uri("lb://appointments-service")
                )

                // ===== GATEWAY HEALTH =====
                .route("gateway-health", r -> r
                        .path("/gateway/health")
                        .filters(f -> f.setStatus(HttpStatus.OK))
                        .uri("no://op")
                )

                .build();
    }
}
