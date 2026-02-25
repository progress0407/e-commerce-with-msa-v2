package io.philo.shop.dto.web;

import java.util.List;

public record OrderCreateRequest(
        Long userId,
        List<OrderLineRequestDto> orderLineRequestDtos
) {
}
