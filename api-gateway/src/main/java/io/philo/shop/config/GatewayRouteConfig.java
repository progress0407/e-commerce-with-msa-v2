package io.philo.shop.config;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    private final RouteLocatorBuilder routeLocatorBuilder;

    @Bean
    public RouteLocator routes() {
        return routeLocatorBuilder.routes()
                .route(r -> route(r, "USER-SERVICE", "/users"))
                .route(r -> route(r, "ITEM-SERVICE", "/items", POST, GET))
                .route(r -> route(r, "ORDER-SERVICE", "/orders", POST, GET))
                .build();
    }

    private Buildable<Route> route(
            PredicateSpec predicateSpec,
            String serviceName,
            String path,
        HttpMethod... httpMethods
    ) {
        return pathSpec(predicateSpec, path, httpMethods)
                .uri("lb://" + serviceName);
    }

    private BooleanSpec pathSpec(
            PredicateSpec predicateSpec,
            String path,
            HttpMethod... httpMethods
    ) {
        BooleanSpec pathSpec = predicateSpec.path(path + "/**");
        if (httpMethods == null || httpMethods.length == 0) {
            return pathSpec;
        }
        return pathSpec.and().method(httpMethods);
    }
}
