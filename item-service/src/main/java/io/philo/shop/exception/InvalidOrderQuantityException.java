package io.philo.shop.exception;

import lombok.Getter;

@Getter
public class InvalidOrderQuantityException extends OrderCancelTriggerException {

    private final int requestedQuantity;

    public InvalidOrderQuantityException(Long itemId, int requestedQuantity) {
        super("주문 수량은 양수여야 합니다. itemId=%s, requestedQuantity=%d"
                .formatted(itemId, requestedQuantity), itemId);
        this.requestedQuantity = requestedQuantity;
    }
}
