package io.philo.shop.exception;

import lombok.Getter;

@Getter
public abstract class OrderCancelTriggerException extends RuntimeException {

    private final Long itemId;

    protected OrderCancelTriggerException(String message, Long itemId) {
        super(message);
        this.itemId = itemId;
    }
}
