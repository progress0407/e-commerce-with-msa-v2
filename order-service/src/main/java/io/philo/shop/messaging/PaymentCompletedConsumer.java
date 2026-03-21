package io.philo.shop.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.philo.shop.PaymentCompletedEvent;
import io.philo.shop.application.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentCompleted(PaymentCompletedEvent event) {
        if (event == null) {
            log.warn("payment-completed 이벤트가 null 입니다. 메시지를 무시합니다.");
            return;
        }
        orderService.completeOrder(event.orderId());
        log.info("결제 완료 이벤트를 처리했습니다. orderId={}, paymentId={}",
                event.orderId(),
                event.paymentId());
    }
}
