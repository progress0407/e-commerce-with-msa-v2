package io.philo.shop.messaging;

import java.util.concurrent.ExecutionException;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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

    public void publishPaymentRequested(PaymentRequestedEvent event) {
        try {
            kafkaTemplate.send(paymentRequestedTopic, String.valueOf(event.orderId()), event).get();
            log.info("결제 요청 이벤트를 발행했습니다. orderId={}, topic={}", event.orderId(), paymentRequestedTopic);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("결제 요청 이벤트를 발행하는 중 인터럽트가 발생했습니다.", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("결제 요청 이벤트 발행에 실패했습니다.", ex);
        }
    }
}
