package io.philo.shop.exception;

public class ItemNotFoundForOrderException extends OrderCancelTriggerException {

    public ItemNotFoundForOrderException(Long itemId) {
        super("상품을 찾을 수 없습니다. itemId=%s".formatted(itemId), itemId);
    }
}
