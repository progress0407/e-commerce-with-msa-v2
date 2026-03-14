package io.philo.shop.messaging;

import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.philo.shop.PaymentFailedEvent;
import io.philo.shop.application.OrderService;

@ExtendWith(MockitoExtension.class)
class PaymentFailedConsumerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentFailedConsumer paymentFailedConsumer;

    @Test
    void consumePaymentFailed_marksOrderAsFail() {
        var event = new PaymentFailedEvent(
                100L,
                "payment-001",
                30000,
                999,
                "잔액 부족",
                List.of(new PaymentFailedEvent.OrderLine(10L, 2))
        );

        paymentFailedConsumer.consumePaymentFailed(event);

        verify(orderService).failOrder(100L);
    }
}
