package com.medical.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter {

    @Value("${jwt.secret:my-secret-key-change-in-production}")
    private String jwtSecret;

    @Value("${jwt.enabled:false}")
    private boolean jwtEnabled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1️⃣ Bypass health endpoints
        String path = exchange.getRequest().getPath().toString();
        if (path.contains("/api/health") || path.contains("/gateway/health")) {
            return chain.filter(exchange);
        }

        // 2️⃣ JWT désactivé
        if (!jwtEnabled) {
            log.debug("JWT validation disabled");
            return chain.filter(exchange);
        }

        // 3️⃣ Lire Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            return onError(exchange,
                    "Missing or invalid Authorization header",
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            // 4️⃣ Valider le token
            String token = authHeader.substring(7);
            validateToken(token);

            log.info("JWT token validated successfully");
            return chain.filter(exchange);

        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid JWT token",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    private void validateToken(String token) {
        Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(
                                jwtSecret.getBytes(StandardCharsets.UTF_8)
                        )
                )
                .build()
                .parseSignedClaims(token);
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               String message,
                               HttpStatus status) {

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse()
                .getHeaders()
                .add("Content-Type", "application/json");

        String errorJson = String.format(
                "{\"error\": \"%s\", \"status\": %d}",
                message,
                status.value()
        );

        return exchange.getResponse()
                .writeWith(Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(errorJson.getBytes())
                ));
    }
}
