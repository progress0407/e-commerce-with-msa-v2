package io.philo.shop.dto;

import io.philo.shop.entity.ItemEntity;

public record ItemResponse(
        Long id,
        String name,
        String size,
        int price,
        int availableQuantity
) {
    public ItemResponse(ItemEntity itemEntity) {
        this(
                itemEntity.getId(),
                itemEntity.getName(),
                itemEntity.getSize(),
                itemEntity.getPrice(),
                itemEntity.getStockQuantity()
        );
    }
}
