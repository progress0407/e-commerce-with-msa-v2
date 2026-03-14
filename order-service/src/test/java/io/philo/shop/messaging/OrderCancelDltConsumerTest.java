package io.philo.shop.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.application.OrderService;
import io.philo.shop.domain.OrderCancelDltLogEntity;
import io.philo.shop.repository.OrderCancelDltLogRepository;

@ExtendWith(MockitoExtension.class)
class OrderCancelDltConsumerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderCancelDltLogRepository orderCancelDltLogRepository;

    @Mock
    private OrderCancelDltAlertNotifier orderCancelDltAlertNotifier;

    @InjectMocks
    private OrderCancelDltConsumer orderCancelDltConsumer;

    @Test
    void consumeOrderCanceledDlt_reprocessesSuccessfully() {
        OrderCanceledEvent event = new OrderCanceledEvent(100L, 10L, "테스트 사유");
        ConsumerRecord<String, OrderCanceledEvent> record =
                new ConsumerRecord<>("order.canceled.v1.DLT", 1, 5L, "100", event);

        OrderCancelDltLogEntity savedEntity = new OrderCancelDltLogEntity(
                event.orderId(), event.itemId(), event.reason(), record.topic(), record.partition(), record.offset()
        );
        when(orderCancelDltLogRepository.save(org.mockito.ArgumentMatchers.any(OrderCancelDltLogEntity.class)))
                .thenReturn(savedEntity);

        orderCancelDltConsumer.consumeOrderCanceledDlt(record);

        verify(orderService).cancelOrder(100L);
        verifyNoInteractions(orderCancelDltAlertNotifier);
        assertThat(savedEntity.isReprocessAttempted()).isTrue();
        assertThat(savedEntity.isReprocessSucceeded()).isTrue();
    }

    @Test
    void consumeOrderCanceledDlt_logsAndAlertsWhenReprocessFails() {
        OrderCanceledEvent event = new OrderCanceledEvent(200L, 20L, "테스트 실패 사유");
        ConsumerRecord<String, OrderCanceledEvent> record =
                new ConsumerRecord<>("order.canceled.v1.DLT", 0, 9L, "200", event);

        OrderCancelDltLogEntity savedEntity = new OrderCancelDltLogEntity(
                event.orderId(), event.itemId(), event.reason(), record.topic(), record.partition(), record.offset()
        );
        when(orderCancelDltLogRepository.save(org.mockito.ArgumentMatchers.any(OrderCancelDltLogEntity.class)))
                .thenReturn(savedEntity);
        doThrow(new RuntimeException("reprocess failed"))
                .when(orderService)
                .cancelOrder(200L);

        assertThatCode(() -> orderCancelDltConsumer.consumeOrderCanceledDlt(record))
                .doesNotThrowAnyException();

        verify(orderCancelDltAlertNotifier)
                .notifyReprocessFailure(eq(event), eq(record.topic()), eq(record.partition()), eq(record.offset()), any(Exception.class));
        assertThat(savedEntity.isReprocessAttempted()).isTrue();
        assertThat(savedEntity.isReprocessSucceeded()).isFalse();
        assertThat(savedEntity.getReprocessErrorMessage()).contains("reprocess failed");
    }
}
