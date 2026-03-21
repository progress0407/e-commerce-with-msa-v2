package io.philo.shop.messaging;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.philo.shop.PaymentCompletedEvent;
import io.philo.shop.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.payment-completed}")
    private String paymentCompletedTopic;

    @Value("${app.kafka.topic.payment-failed}")
    private String paymentFailedTopic;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        sendAndWait(paymentCompletedTopic, String.valueOf(event.orderId()), event, "결제 완료 이벤트");
        log.info("결제 완료 이벤트를 발행했습니다. orderId={}, topic={}", event.orderId(), paymentCompletedTopic);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        sendAndWait(paymentFailedTopic, String.valueOf(event.orderId()), event, "결제 실패 이벤트");
        log.info("결제 실패 이벤트를 발행했습니다. orderId={}, topic={}", event.orderId(), paymentFailedTopic);
    }

    private void sendAndWait(String topic, String key, Object payload, String eventName) {
        try {
            kafkaTemplate.send(topic, key, payload).get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(eventName + "를 발행하는 중 인터럽트가 발생했습니다.", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException(eventName + " 발행에 실패했습니다.", ex);
        }
    }
}
