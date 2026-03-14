package io.philo.shop.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import io.philo.shop.constant.OrderStatus;
import io.philo.shop.domain.OrderEntity;
import io.philo.shop.exception.OrderNotFoundForCancelException;
import io.philo.shop.messaging.OrderEventProducer;
import io.philo.shop.repository.OrderRepository;
import io.philo.shop.testsupport.ManualOrderServiceExceptionTrigger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private ManualOrderServiceExceptionTrigger manualOrderServiceExceptionTrigger;

    @InjectMocks
    private OrderService orderService;

    @Test
    void cancelOrder_marksOrderAsCanceled() {
        OrderEntity orderEntity = OrderEntity.empty();
        when(orderRepository.findById(2L)).thenReturn(Optional.of(orderEntity));

        orderService.cancelOrder(1L);

        assertThat(orderEntity.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    void cancelOrder_throwsCustomExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(OrderNotFoundForCancelException.class)
                .hasMessageContaining("orderId=1");
    }
}
