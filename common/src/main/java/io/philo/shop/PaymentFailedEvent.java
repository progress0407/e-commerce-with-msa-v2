package io.philo.shop;

public record PaymentFailedEvent(
        Long orderId,
        String paymentId,
        int totalAmount,
        int resultCode,
        String resultMessage
) {
}
