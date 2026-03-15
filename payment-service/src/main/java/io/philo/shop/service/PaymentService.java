package io.philo.shop.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.philo.shop.PaymentCompletedEvent;
import io.philo.shop.PaymentFailedEvent;
import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.constant.PaymentStatus;
import io.philo.shop.dto.PaymentGatewayRequest;
import io.philo.shop.dto.PaymentGatewayResponse;
import io.philo.shop.entity.PaymentEntity;
import io.philo.shop.messaging.PaymentServiceEventProducer;
import io.philo.shop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentServiceEventProducer paymentServiceEventProducer;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void executePayment(PaymentRequestedEvent event) {
        log.info("결제 요청 이벤트를 수신했습니다. orderId={}", event.orderId());

        var cardId = "Someone's Card ID";
        var billingKey = findByBillingKeyByCardId(cardId);
        var requestDto = getPaymentGatewayRequest(event);
        var responseDto = requestPaymentGateway(requestDto);
		var paymentEntity = toPaymentEntity(event, requestDto, responseDto);
        var savedPayment = paymentRepository.save(paymentEntity);

        if (responseDto.isSuccess()) {
            paymentServiceEventProducer.publishPaymentCompleted(new PaymentCompletedEvent(
                    event.orderId(),
                    responseDto.paymentId(),
                    event.totalAmount()
            ));
            log.info("결제가 성공했습니다. orderId={}, paymentId={}, paymentEntityId={}",
                    event.orderId(), responseDto.paymentId(), savedPayment.getId());
            return;
        }

        paymentServiceEventProducer.publishPaymentFailed(new PaymentFailedEvent(
                event.orderId(),
                responseDto.paymentId(),
                event.totalAmount(),
                responseDto.resultCode(),
                responseDto.resultMessage(),
                toPaymentFailedOrderLines(event.orderLines())
        ));
        log.warn("결제가 실패했습니다. orderId={}, paymentId={}, resultCode={}, message={}, paymentEntityId={}",
                event.orderId(), responseDto.paymentId(), responseDto.resultCode(), responseDto.resultMessage(), savedPayment.getId());
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

    private static List<PaymentFailedEvent.OrderLine> toPaymentFailedOrderLines(
            List<PaymentRequestedEvent.OrderLine> orderLines
    ) {
        if (orderLines == null || orderLines.isEmpty()) {
            return Collections.emptyList();
        }
        return orderLines.stream()
                .map(orderLine -> new PaymentFailedEvent.OrderLine(orderLine.itemId(), orderLine.quantity()))
                .toList();
    }

    private static PaymentEntity toPaymentEntity(
            PaymentRequestedEvent event,
            PaymentGatewayRequest requestDto,
            PaymentGatewayResponse responseDto
    ) {
		var paymentStatus = responseDto.isSuccess() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        return new PaymentEntity(
                event.orderId(),
                requestDto.paymentId(),
                requestDto.orderName(),
                requestDto.customer(),
                requestDto.totalAmount(),
                requestDto.currency(),
                paymentStatus,
                responseDto.resultCode(),
                responseDto.resultMessage()
        );
    }
}
