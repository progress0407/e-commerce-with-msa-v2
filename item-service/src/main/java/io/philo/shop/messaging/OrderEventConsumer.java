package io.philo.shop.messaging;

import java.util.List;

import io.philo.OrderCreatedEvent;
import io.philo.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ItemService itemService;

    @KafkaListener(topics = "${app.kafka.topic.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        List<OrderCreatedEvent.OrderLine> orderLines = event.orderLines();

        if (orderLines == null || orderLines.isEmpty()) {
            log.info("주문 생성 이벤트에 주문 라인이 없습니다. orderId={}", event.orderId());
            return;
        }

        itemService.decreaseStockByOrder(orderLines);
        log.info("주문 생성 이벤트를 처리했습니다. orderId={}, orderLineCount={}", event.orderId(), orderLines.size());
    }
}
