package io.philo.shop;

import java.util.List;

public record PaymentFailedEvent(
        Long orderId,
        String paymentId,
        int totalAmount,
        int resultCode,
        String resultMessage,
        List<OrderLine> orderLines
) {

    public record OrderLine(
            Long itemId,
            int quantity
    ) {
    }
}
