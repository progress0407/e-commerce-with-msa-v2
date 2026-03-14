package io.philo.shop.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.philo.shop.PaymentFailedEvent;
import io.philo.shop.application.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentFailed(PaymentFailedEvent event) {
        orderService.failOrder(event.orderId());
        log.info("결제 실패 이벤트를 처리했습니다. orderId={}, paymentId={}, resultCode={}",
                event.orderId(),
                event.paymentId(),
                event.resultCode());
    }
}
