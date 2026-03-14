package io.philo.shop.service;

import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.philo.shop.PaymentCompletedEvent;
import io.philo.shop.PaymentFailedEvent;
import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.dto.PaymentGatewayRequest;
import io.philo.shop.dto.PaymentGatewayResponse;
import io.philo.shop.messaging.PaymentServiceEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentServiceEventProducer paymentServiceEventProducer;

	@Transactional
    public void executePayment(PaymentRequestedEvent event) {

        log.info("결제 요청 이벤트를 수신했습니다. orderId={}", event.orderId());

        var cardId = "Someone's Card ID";
        var billingKey = findByBillingKeyByCardId(cardId);

		var requestDto = getPaymentGatewayRequest(event);
        var responseDto = requestPaymentGateway(requestDto);

        if (responseDto.isSuccess()) {
            paymentServiceEventProducer.publishPaymentCompleted(new PaymentCompletedEvent(
                    event.orderId(),
                    responseDto.paymentId(),
                    event.totalAmount()
            ));
            log.info("결제가 성공했습니다. orderId={}, paymentId={}", event.orderId(), responseDto.paymentId());
            return;
        }

        paymentServiceEventProducer.publishPaymentFailed(new PaymentFailedEvent(
                event.orderId(),
                responseDto.paymentId(),
                event.totalAmount(),
                responseDto.resultCode(),
                responseDto.resultMessage()
        ));
        log.warn("결제가 실패했습니다. orderId={}, paymentId={}, resultCode={}, message={}",
                event.orderId(), responseDto.paymentId(), responseDto.resultCode(), responseDto.resultMessage());
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
