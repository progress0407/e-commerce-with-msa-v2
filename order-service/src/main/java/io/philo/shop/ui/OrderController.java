package io.philo.shop.ui;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.philo.shop.application.OrderService;
import io.philo.shop.dto.ResourceCreateResponse;
import io.philo.shop.dto.web.OrderCreateRequest;
import io.philo.shop.dto.web.OrderDetailResponse;
import io.philo.shop.dto.web.OrderListResponses;
import io.philo.shop.query.OrderQuery;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderQuery orderQuery;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreateResponse order(@RequestBody OrderCreateRequest request) {
        Long orderId = orderService.order(request.orderLineRequestDtos());
        return new ResourceCreateResponse(orderId);
    }

    @GetMapping
    public OrderListResponses list() {
        return orderQuery.list();
    }

    @GetMapping("/{id}")
    public OrderDetailResponse detail(@PathVariable("id") Long id) {
        return orderQuery.detail(id);
    }
}
