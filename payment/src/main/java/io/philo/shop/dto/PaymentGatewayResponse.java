package io.philo.shop.dto;

public record PaymentGatewayResponse(String paymentId,
									 boolean isSuccess,
									 int resultCode,
									 String resultMessage
									 ) {
}
