package io.philo.shop.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RouteInspector {

    private final RouteLocator routeLocator;

    @PostConstruct
    public void inspect() {
        routeLocator.getRoutes().subscribe((Route route) ->
                log.info("Route ID: {} , URI: {}", route.getId(), route.getUri()));
    }
}
