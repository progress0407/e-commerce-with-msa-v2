package io.philo.shop.dto;

import io.philo.shop.entity.ItemEntity;

public record ItemInternalResponseDto(
        Long id,
        String name,
        String size,
        int price,
        int stockQuantity
) {

    public ItemInternalResponseDto(ItemEntity itemEntity) {
        this(
                itemEntity.getId(),
                itemEntity.getName(),
                itemEntity.getSize(),
                itemEntity.getPrice(),
                itemEntity.getStockQuantity()
        );
    }
}
