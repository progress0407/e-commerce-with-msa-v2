package io.philo.shop;

public record OrderCanceledEvent(
        Long orderId,
        Long itemId,
        String reason
) {
}
