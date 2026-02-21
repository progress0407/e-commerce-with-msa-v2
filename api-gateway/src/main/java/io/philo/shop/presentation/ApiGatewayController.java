package io.philo.shop.presentation;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-gateway")
@RefreshScope
public class ApiGatewayController {

    private final String testConstantValue;
    private final RouteLocator routeLocator;
    private final DiscoveryClient discoveryClient;
    private final EurekaClient eurekaClient;

    public ApiGatewayController(
            @Value("${constant.test.value}") String testConstantValue,
            RouteLocator routeLocator,
            DiscoveryClient discoveryClient,
            EurekaClient eurekaClient
    ) {
        this.testConstantValue = testConstantValue;
        this.routeLocator = routeLocator;
        this.discoveryClient = discoveryClient;
        this.eurekaClient = eurekaClient;
    }

    @GetMapping("/env-test")
    public String testConstantValue() {
        return "check value for dynamical refresh test: " + testConstantValue;
    }

    @GetMapping("/route-info")
    public RouteLocator routeLocator() {
        return routeLocator;
    }

    @GetMapping("/routes/service-instances")
    public Map<String, List<ServiceInstance>> instances3() {
        Map<String, List<ServiceInstance>> result = new LinkedHashMap<>();
        for (String serviceId : discoveryClient.getServices()) {
            result.put(serviceId, discoveryClient.getInstances(serviceId));
        }
        return result;
    }

    @GetMapping("/routes/applications")
    public Map<String, Application> instances4() {
        Map<String, Application> result = new LinkedHashMap<>();
        for (String serviceId : discoveryClient.getServices()) {
            result.put(serviceId, eurekaClient.getApplication(serviceId));
        }
        return result;
    }
}
