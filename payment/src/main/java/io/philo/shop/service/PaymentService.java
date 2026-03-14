package io.philo.shop.service;

import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.dto.PaymentGatewayRequest;
import io.philo.shop.dto.PaymentGatewayResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

    public void process(PaymentRequestedEvent event) {

        log.info("결제 요청 이벤트를 수신했습니다. orderId={}", event.orderId());

        var cardId = "Someone's Card ID";
        var billingKey = findByBillingKeyByCardId(cardId);

		var requestDto = getPaymentGatewayRequest(event);
        var responseDto = requestPaymentGateway(requestDto);

		if (responseDto.isSuccess()) {
			// order-service 에 결제가 성공했음을 알리는 이벤트 전송
		} else {
			// order-service, item-service에 결제가 실패했음을 알리는 이벤트 전송
		}
    }

	private PaymentGatewayResponse requestPaymentGateway(PaymentGatewayRequest requestDto) {

		boolean isSuccess = true;

		if (isSuccess) {
			return new PaymentGatewayResponse(requestDto.paymentId(), true, 0, "결제에 성공했습니다.");
		}

		return new PaymentGatewayResponse(requestDto.paymentId(), false, 999, "계좌 잔액이 부족합니다.");
	}

    private static String findByBillingKeyByCardId(String cardId) {
        // cardId -> billingKey 를 가져오는 임의의 로직
        var someBillingKey = UUID.randomUUID().toString();
        return someBillingKey;
    }

    private static @NonNull PaymentGatewayRequest getPaymentGatewayRequest(PaymentRequestedEvent event) {
		var paymentId = UUID.randomUUID().toString();
        return new PaymentGatewayRequest(paymentId,
            "간편결제",
            "임의의 고객",
            event.totalAmount(),
            "KRW"
        );
    }
}
