package io.philo.shop.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends OrderCancelTriggerException {
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(Long itemId, int requestedQuantity, int availableQuantity) {
        super("상품 재고가 부족합니다. itemId=%s, requestedQuantity=%d, availableQuantity=%d"
                .formatted(itemId, requestedQuantity, availableQuantity), itemId);
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
}
