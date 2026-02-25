package io.philo.shop.dto.web;

import io.philo.shop.domain.OrderEntity;

public record OrderListResponse(
        Long id,
        int totalOrderAmount
) {
    public OrderListResponse(OrderEntity entity) {
        this(entity.getId(), entity.getTotalOrderAmount());
    }
}
