package io.philo.shop.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.application.OrderService;
import io.philo.shop.domain.OrderCancelDltLogEntity;
import io.philo.shop.repository.OrderCancelDltLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelDltConsumer {

    private final OrderService orderService;
    private final OrderCancelDltLogRepository orderCancelDltLogRepository;
    private final OrderCancelDltAlertNotifier orderCancelDltAlertNotifier;

    @KafkaListener(
            topics = "${app.kafka.topic.order-canceled-dlt}",
            groupId = "${spring.kafka.consumer.group-id}.dlt"
    )
    public void consumeOrderCanceledDlt(ConsumerRecord<String, OrderCanceledEvent> record) {
        OrderCanceledEvent event = record.value();
        if (event == null) {
            log.warn("DLT 메시지 payload가 비어 있습니다. topic={}, partition={}, offset={}",
                    record.topic(), record.partition(), record.offset());
            return;
        }

        OrderCancelDltLogEntity dltLog = orderCancelDltLogRepository.save(
                new OrderCancelDltLogEntity(
                        event.orderId(),
                        event.itemId(),
                        event.reason(),
                        record.topic(),
                        record.partition(),
                        record.offset()
                )
        );

        try {
            orderService.cancelOrder(event.orderId());
            dltLog.markReprocessSucceeded();
            orderCancelDltLogRepository.save(dltLog);
            log.info("DLT 메시지 재처리에 성공했습니다. dltLogId={}, orderId={}, topic={}, partition={}, offset={}",
                    dltLog.getId(), event.orderId(), record.topic(), record.partition(), record.offset());
        } catch (Exception ex) {
            dltLog.markReprocessFailed(ex.getMessage());
            orderCancelDltLogRepository.save(dltLog);
            orderCancelDltAlertNotifier.notifyReprocessFailure(
                    event, record.topic(), record.partition(), record.offset(), ex
            );
            // DLT 컨슈머에서 예외를 재던지지 않음: 동일 DLT 레코드 무한 재소비 방지
            log.warn("DLT 메시지 재처리에 실패했습니다. dltLogId={}, orderId={}, error={}",
                    dltLog.getId(), event.orderId(), ex.getMessage());
        }
    }
}
