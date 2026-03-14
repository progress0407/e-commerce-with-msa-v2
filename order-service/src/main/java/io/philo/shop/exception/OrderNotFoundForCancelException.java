package io.philo.shop.exception;

import lombok.Getter;

@Getter
public class OrderNotFoundForCancelException extends RuntimeException {

    private final Long orderId;

    public OrderNotFoundForCancelException(Long orderId) {
        super("롤백할 주문을 찾을 수 없습니다. orderId=" + orderId);
        this.orderId = orderId;
    }
}
