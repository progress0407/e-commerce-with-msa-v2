package io.philo.shop.testsupport;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.philo.shop.exception.OrderNotFoundForCancelException;

class ManualOrderServiceExceptionTriggerTest {

    @Test
    void throwIfConfigured_throwsOrderNotFoundExceptionWhenSimpleNameIsConfigured() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("OrderNotFoundForCancelException");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(OrderNotFoundForCancelException.class);
    }

    @Test
    void throwIfConfigured_throwsOrderNotFoundExceptionWhenFqcnIsConfigured() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("io.philo.shop.exception.OrderNotFoundForCancelException");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(OrderNotFoundForCancelException.class);
    }

    @Test
    void throwIfConfigured_acceptsLegacyClassToStringFormat() {
        ManualOrderServiceExceptionTrigger trigger =
            createTrigger("class io.philo.shop.exception.OrderNotFoundForCancelException");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(OrderNotFoundForCancelException.class);
    }

    @Test
    void throwIfConfigured_doesNothingWhenUnknownType() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("UNKNOWN_TYPE");

        assertThatCode(trigger::throwIfConfigured)
            .doesNotThrowAnyException();
    }

    @Test
    void throwIfConfigured_doesNothingWhenBlank() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger(" ");

        assertThatCode(trigger::throwIfConfigured)
            .doesNotThrowAnyException();
    }

    private static ManualOrderServiceExceptionTrigger createTrigger(String configuredExceptionType) {
        ManualOrderServiceExceptionTrigger trigger = new ManualOrderServiceExceptionTrigger();
        ReflectionTestUtils.setField(trigger, "configuredExceptionType", configuredExceptionType);
        return trigger;
    }
}
