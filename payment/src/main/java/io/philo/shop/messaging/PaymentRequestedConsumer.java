package io.philo.shop.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestedConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "${app.kafka.topic.payment-requested}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentRequested(PaymentRequestedEvent event) {
        paymentService.process(event);
    }
}
