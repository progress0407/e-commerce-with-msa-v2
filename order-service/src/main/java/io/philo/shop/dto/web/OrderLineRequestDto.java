package io.philo.shop.dto.web;

public record OrderLineRequestDto(
        Long itemId,
        int itemQuantity,
        int itemAmount,
        int itemDiscountedAmount
) {
}
