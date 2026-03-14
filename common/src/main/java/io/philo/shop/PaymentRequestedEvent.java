package io.philo.shop;

import java.util.List;

public record PaymentRequestedEvent(
        Long orderId,
        int totalAmount,
        List<OrderLine> orderLines
) {

    public record OrderLine(
            Long itemId,
            int quantity
    ) {
    }
}
