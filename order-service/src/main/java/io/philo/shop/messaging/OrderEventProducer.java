package io.philo.shop.messaging;

import java.util.concurrent.ExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.philo.OrderCreatedEvent;

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
            log.info("Published order-created event. orderId={}, topic={}", event.orderId(), orderCreatedTopic);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing order-created event.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to publish order-created event.", e);
        }
    }
}
