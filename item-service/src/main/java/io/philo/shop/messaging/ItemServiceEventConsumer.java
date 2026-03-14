package io.philo.shop.messaging;

import java.util.List;

import io.philo.shop.OrderCreatedEvent;
import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.PaymentFailedEvent;
import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.exception.OrderCancelTriggerException;
import io.philo.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceEventConsumer {

    private final ItemService itemService;
    private final ItemServiceEventProducer itemServiceEventProducer;

    @KafkaListener(topics = "${app.kafka.topic.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        List<OrderCreatedEvent.OrderLine> orderLines = event.orderLines();

        if (orderLines == null || orderLines.isEmpty()) {
            log.info("주문 생성 이벤트에 주문 라인이 없습니다. orderId={}", event.orderId());
            return;
        }

        try {
            itemService.decreaseStockByOrder(orderLines);
            log.info("주문 생성 이벤트를 처리했습니다. orderId={}, orderLineCount={}", event.orderId(), orderLines.size());
            itemServiceEventProducer.publishPaymentRequested(new PaymentRequestedEvent(
                    event.orderId(),
                    event.totalAmount(),
                    toPaymentOrderLines(orderLines)
            ));
        } catch (OrderCancelTriggerException ex) {
			var orderCanceledEvent = new OrderCanceledEvent(event.orderId(), ex.getItemId(), ex.getMessage());
            itemServiceEventProducer.publishOrderCanceled(orderCanceledEvent);
            log.warn("상품 재고 차감 중 예외가 발생하여 롤백 이벤트를 발행했습니다. orderId={}, itemId={}", event.orderId(), ex.getItemId());
        }
    }

    @KafkaListener(topics = "${app.kafka.topic.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaymentFailed(PaymentFailedEvent event) {
        if (event.orderLines() == null || event.orderLines().isEmpty()) {
            log.warn("복구할 주문 라인이 없어 재고 복구를 건너뜁니다. orderId={}, paymentId={}",
                event.orderId(), event.paymentId());
            return;
        }

        itemService.restoreStockByPaymentFailure(event.orderLines());
        log.info("결제 실패로 재고를 복구했습니다. orderId={}, paymentId={}, lineCount={}",
            event.orderId(),
            event.paymentId(),
            event.orderLines().size());
    }

    private static List<PaymentRequestedEvent.OrderLine> toPaymentOrderLines(List<OrderCreatedEvent.OrderLine> orderLines) {
        return orderLines.stream()
                .map(orderLine -> new PaymentRequestedEvent.OrderLine(orderLine.itemId(), orderLine.quantity()))
                .toList();
    }
}
