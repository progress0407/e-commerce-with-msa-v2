package io.philo.shop.dto;

public record ItemCreateRequest(
        String name,
        String size,
        int price,
        int stockQuantity
) {
}
