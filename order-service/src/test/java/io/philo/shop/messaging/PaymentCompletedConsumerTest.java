package io.philo.shop.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.philo.shop.PaymentCompletedEvent;
import io.philo.shop.application.OrderService;

@ExtendWith(MockitoExtension.class)
class PaymentCompletedConsumerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentCompletedConsumer paymentCompletedConsumer;

    @Test
    void consumePaymentCompleted_completesOrder() {
        var event = new PaymentCompletedEvent(100L, "payment-001", 30000);

        paymentCompletedConsumer.consumePaymentCompleted(event);

        verify(orderService).completeOrder(100L);
    }
}
