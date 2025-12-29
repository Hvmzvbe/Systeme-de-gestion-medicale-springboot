package com.medical.apigateway.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements GatewayFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String key = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

        Bucket bucket = cache.computeIfAbsent(key, k -> createBucket());

        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(100)                      // 100 requêtes
                .refillGreedy(100, Duration.ofMinutes(1)) // par minute
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
