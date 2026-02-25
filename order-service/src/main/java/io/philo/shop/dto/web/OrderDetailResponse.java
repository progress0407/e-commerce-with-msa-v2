package io.philo.shop.dto.web;

import io.philo.shop.domain.OrderEntity;

public record OrderDetailResponse(OrderEntity entity) {
}
