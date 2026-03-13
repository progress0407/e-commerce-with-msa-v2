package io.philo.shop.messaging;

import java.util.concurrent.ExecutionException;

import io.philo.shop.OrderCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceEventProducer {

    private final KafkaTemplate<String, OrderCanceledEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-canceled}")
    private String orderCanceledTopic;

	public void publishOrderCanceled(OrderCanceledEvent event) {
        try {
            kafkaTemplate.send(orderCanceledTopic, String.valueOf(event.orderId()), event).get();
            log.info("주문 롤백 이벤트를 발행했습니다. orderId={}, itemId={}, topic={}",
                    event.orderId(),
                    event.itemId(),
                    orderCanceledTopic);
        } catch (InterruptedException ex) {
            // 인터럽트 상태를 복원 (중단 신호를 감지할 수 있게 하기 위함)
            Thread.currentThread().interrupt();
            throw new IllegalStateException("주문 롤백 이벤트를 발행하는 중 인터럽트가 발생했습니다.", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("주문 롤백 이벤트 발행에 실패했습니다.", ex);
        }
    }
}
