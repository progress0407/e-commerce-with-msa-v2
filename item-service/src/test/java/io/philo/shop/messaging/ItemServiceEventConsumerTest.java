package io.philo.shop.messaging;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import io.philo.shop.OrderCanceledEvent;
import io.philo.shop.OrderCreatedEvent;
import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.exception.InvalidOrderQuantityException;
import io.philo.shop.exception.ItemNotFoundForOrderException;
import io.philo.shop.exception.InsufficientStockException;
import io.philo.shop.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceEventConsumerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ItemServiceEventProducer orderCancelProducer;

    @InjectMocks
    private ItemServiceEventConsumer itemServiceEventConsumer;

    @Test
    void consumeOrderCreated_publishesCanceledEventWhenStockIsInsufficient() {
        List<OrderCreatedEvent.OrderLine> orderLines = List.of(new OrderCreatedEvent.OrderLine(10L, 3));
        OrderCreatedEvent event = new OrderCreatedEvent(100L, 30000, orderLines);
        doThrow(new InsufficientStockException(10L, 3, 1))
                .when(itemService)
                .decreaseStockByOrder(orderLines);

        itemServiceEventConsumer.consumeOrderCreated(event);

        ArgumentCaptor<OrderCanceledEvent> captor =
                ArgumentCaptor.forClass(OrderCanceledEvent.class);
        verify(orderCancelProducer).publishOrderCanceled(captor.capture());
        verify(orderCancelProducer, never()).publishPaymentRequested(any());

        assertThat(captor.getValue().orderId()).isEqualTo(100L);
        assertThat(captor.getValue().itemId()).isEqualTo(10L);
        assertThat(captor.getValue().reason()).contains("상품 재고가 부족합니다.");
    }

    @Test
    void consumeOrderCreated_doesNotPublishCanceledEventWhenStockDecreaseSucceeds() {
        List<OrderCreatedEvent.OrderLine> orderLines = List.of(new OrderCreatedEvent.OrderLine(10L, 1));
        OrderCreatedEvent event = new OrderCreatedEvent(100L, 30000, orderLines);

        itemServiceEventConsumer.consumeOrderCreated(event);

        verify(itemService).decreaseStockByOrder(orderLines);
        verify(orderCancelProducer).publishPaymentRequested(new PaymentRequestedEvent(
                100L,
                30000,
                List.of(new PaymentRequestedEvent.OrderLine(10L, 1))
        ));
        verify(orderCancelProducer, never()).publishOrderCanceled(any());
    }

    @Test
    void consumeOrderCreated_publishesCanceledEventWhenOrderQuantityIsInvalid() {
        List<OrderCreatedEvent.OrderLine> orderLines = List.of(new OrderCreatedEvent.OrderLine(10L, 0));
        OrderCreatedEvent event = new OrderCreatedEvent(100L, 30000, orderLines);
        doThrow(new InvalidOrderQuantityException(10L, 0))
                .when(itemService)
                .decreaseStockByOrder(orderLines);

        itemServiceEventConsumer.consumeOrderCreated(event);

        ArgumentCaptor<OrderCanceledEvent> captor = ArgumentCaptor.forClass(OrderCanceledEvent.class);
        verify(orderCancelProducer).publishOrderCanceled(captor.capture());
        verify(orderCancelProducer, never()).publishPaymentRequested(any());
        assertThat(captor.getValue().itemId()).isEqualTo(10L);
        assertThat(captor.getValue().reason()).contains("주문 수량은 양수여야 합니다.");
    }

    @Test
    void consumeOrderCreated_publishesCanceledEventWhenItemIsNotFound() {
        List<OrderCreatedEvent.OrderLine> orderLines = List.of(new OrderCreatedEvent.OrderLine(999L, 1));
        OrderCreatedEvent event = new OrderCreatedEvent(100L, 30000, orderLines);
        doThrow(new ItemNotFoundForOrderException(999L))
                .when(itemService)
                .decreaseStockByOrder(orderLines);

        itemServiceEventConsumer.consumeOrderCreated(event);

        ArgumentCaptor<OrderCanceledEvent> captor = ArgumentCaptor.forClass(OrderCanceledEvent.class);
        verify(orderCancelProducer).publishOrderCanceled(captor.capture());
        verify(orderCancelProducer, never()).publishPaymentRequested(any());
        assertThat(captor.getValue().itemId()).isEqualTo(999L);
        assertThat(captor.getValue().reason()).contains("상품을 찾을 수 없습니다.");
    }
}
