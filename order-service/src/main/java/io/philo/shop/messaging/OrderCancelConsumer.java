package io.philo.shop.messaging;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.application.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.order-canceled}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderCanceled(OrderCanceledEvent event) {
        orderService.cancelOrder(event.orderId());
        log.info("주문 롤백 이벤트를 처리했습니다. orderId={}, itemId={}, reason={}",
                event.orderId(),
                event.itemId(),
                event.reason());
    }
}
