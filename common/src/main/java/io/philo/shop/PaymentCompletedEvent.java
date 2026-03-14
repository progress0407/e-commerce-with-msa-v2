package io.philo.shop;

public record PaymentCompletedEvent(
        Long orderId,
        String paymentId,
        int totalAmount
) {
}
