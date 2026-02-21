package io.philo.shop.support;

import io.philo.shop.error.InAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalExceptionHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.info("Unhandled exception in gateway", ex);

        ServerHttpResponse response = exchange.getResponse();
        setJsonContentType(response);
        setHttpStatusCode(ex, response);
        Mono<DataBuffer> responseBody = createResponseBody(ex, response);

        return response.writeWith(responseBody);
    }

    private void setJsonContentType(ServerHttpResponse response) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    private void setHttpStatusCode(Throwable ex, ServerHttpResponse response) {
        if (ex instanceof InAppException inAppException) {
            response.setStatusCode(inAppException.getHttpStatus());
            return;
        }
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<DataBuffer> createResponseBody(Throwable ex, ServerHttpResponse response) {
        String errorMessage = ex.getMessage() == null ? "Unknown error" : ex.getMessage();
        String responseJsonString = "{\"errorMessage\":\"" + escapeJson(errorMessage) + "\"}";
        DataBuffer buffer = convertDataBuffer(response, responseJsonString);
        return Mono.just(buffer);
    }

    private DataBuffer convertDataBuffer(ServerHttpResponse response, String string) {
        return response.bufferFactory().wrap(string.getBytes(StandardCharsets.UTF_8));
    }

    private String escapeJson(String raw) {
        return raw
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
