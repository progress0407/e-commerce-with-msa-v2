package io.philo.shop.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.application.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.order-canceled}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderCanceled(OrderCanceledEvent event) {
        if (event == null) {
            log.warn("order-canceled 이벤트가 null 입니다. 메시지를 무시합니다.");
            return;
        }
        orderService.cancelOrder(event.orderId());
        log.info("주문 롤백 이벤트를 처리했습니다. orderId={}, itemId={}, reason={}",
                event.orderId(),
                event.itemId(),
                event.reason());
   }
}
