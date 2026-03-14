package io.philo.shop.messaging;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.philo.shop.PaymentRequestedEvent;
import io.philo.shop.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentRequestedConsumerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentRequestedConsumer paymentRequestedConsumer;

    @Test
    void consumePaymentRequested_callsProcessingService() {
        var event = new PaymentRequestedEvent(100L);

        paymentRequestedConsumer.consumePaymentRequested(event);

        verify(paymentService).process(event);
    }

    @Test
    void consumePaymentRequested_ignoresEmptyPayload() {
        paymentRequestedConsumer.consumePaymentRequested(null);

        verifyNoInteractions(paymentService);
    }
}
