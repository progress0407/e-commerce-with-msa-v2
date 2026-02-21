package io.philo.shop.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();

        log.info(
                "[ Start ] id: {}, path: {}, uri: {}, method: {}, localAddress: {}, remoteAddress: {}",
                request.getId(),
                request.getPath(),
                request.getURI(),
                request.getMethod(),
                request.getLocalAddress(),
                request.getRemoteAddress()
        );

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            var response = exchange.getResponse();
            log.info(
                    "[ End ] id: {}, response.status: {}, response.isCommitted: {}",
                    request.getId(),
                    response.getStatusCode(),
                    response.isCommitted()
            );
        }));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
