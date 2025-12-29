package com.medical.apigateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler{

    private final ServerCodecConfigurer serverCodecConfigurer;

    public GlobalExceptionHandler(ServerCodecConfigurer serverCodecConfigurer) {
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status = determineHttpStatus(ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "Unexpected error")
                .path(exchange.getRequest().getPath().toString())
                .timestamp(System.currentTimeMillis())
                .build();

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            return Mono.error(e);
        }

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
        }

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("401") || ex.getMessage().contains("Unauthorized")) {
                return HttpStatus.UNAUTHORIZED;
            }
            if (ex.getMessage().contains("403") || ex.getMessage().contains("Forbidden")) {
                return HttpStatus.FORBIDDEN;
            }
            if (ex.getMessage().contains("404") || ex.getMessage().contains("Not Found")) {
                return HttpStatus.NOT_FOUND;
            }
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}