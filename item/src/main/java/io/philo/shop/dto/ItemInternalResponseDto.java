package io.philo.shop.dto;

public record ItemInternalResponseDto(
        Long id,
        String name,
        String size,
        int price
) {
}
