package io.philo.shop.dto;

public record PaymentGatewayRequest(String paymentId,
									String orderName,
									String customer,
									int totalAmount,
									String currency) {
}
