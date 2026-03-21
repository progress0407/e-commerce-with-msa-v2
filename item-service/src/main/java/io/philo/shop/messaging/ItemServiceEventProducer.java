package io.philo.shop.messaging;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.order-canceled}")
    private String orderCanceledTopic;

    @Value("${app.kafka.topic.payment-requested}")
    private String paymentRequestedTopic;

	public void publishOrderCanceled(OrderCanceledEvent event) {
        sendAndWait(orderCanceledTopic, String.valueOf(event.orderId()), event, "주문 롤백 이벤트");
        log.info("주문 롤백 이벤트를 발행했습니다. orderId={}, itemId={}, topic={}",
                event.orderId(),
                event.itemId(),
                orderCanceledTopic);
    }

    public void publishPaymentRequested(PaymentRequestedEvent event) {
        sendAndWait(paymentRequestedTopic, String.valueOf(event.orderId()), event, "결제 요청 이벤트");
        log.info("결제 요청 이벤트를 발행했습니다. orderId={}, topic={}", event.orderId(), paymentRequestedTopic);
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
