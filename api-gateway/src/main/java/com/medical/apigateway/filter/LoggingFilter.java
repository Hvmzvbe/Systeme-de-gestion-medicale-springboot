package com.medical.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("=== GATEWAY REQUEST ===");
        log.info("Request ID: {}", exchange.getRequest().getId());
        log.info("Method: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath());
        log.info("Headers: {}", exchange.getRequest().getHeaders());

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    log.info("=== GATEWAY RESPONSE ===");
                    log.info("Response Status: {}",
                            exchange.getResponse().getStatusCode());
                });
    }
}
