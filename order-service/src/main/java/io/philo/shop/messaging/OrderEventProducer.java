package io.philo.shop.messaging;

import java.util.concurrent.ExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.philo.shop.OrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-created}")
    private String orderCreatedTopic;

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            kafkaTemplate.send(orderCreatedTopic, String.valueOf(event.orderId()), event).get();
            log.info("주문 생성 이벤트를 발행했습니다. orderId={}, topic={}", event.orderId(), orderCreatedTopic);
        } catch (InterruptedException e) {
            // 인터럽트 상태를 복원 (중단 신호를 감지할 수 있게 하기 위함)
            Thread.currentThread().interrupt();
            throw new IllegalStateException("주문 생성 이벤트를 발행하는 중 인터럽트가 발생했습니다.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("주문 생성 이벤트 발행에 실패했습니다.", e);
        }
    }
}
