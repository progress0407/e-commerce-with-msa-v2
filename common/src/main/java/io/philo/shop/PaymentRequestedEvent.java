package io.philo.shop;

public record PaymentRequestedEvent(
        Long orderId,
		int totalAmount
) {
}
