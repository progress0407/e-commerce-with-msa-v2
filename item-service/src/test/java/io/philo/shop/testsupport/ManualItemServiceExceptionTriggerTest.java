package io.philo.shop.testsupport;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.philo.shop.exception.InsufficientStockException;
import io.philo.shop.exception.InvalidOrderQuantityException;
import io.philo.shop.exception.ItemNotFoundForOrderException;

class ManualItemServiceExceptionTriggerTest {

    @Test
    void throwIfConfigured_throwsInvalidOrderQuantityException() {
        ManualItemServiceExceptionTrigger injector = createInjector("InvalidOrderQuantityException");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(InvalidOrderQuantityException.class);
    }

    @Test
    void throwIfConfigured_throwsInsufficientStockException() {
        ManualItemServiceExceptionTrigger injector = createInjector("INSUFFICIENT_STOCK");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void throwIfConfigured_throwsItemNotFoundException() {
        ManualItemServiceExceptionTrigger injector = createInjector("ItemNotFoundForOrderException");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(ItemNotFoundForOrderException.class);
    }

    @Test
    void throwIfConfigured_acceptsLegacyClassToStringFormat() {
        ManualItemServiceExceptionTrigger injector =
            createInjector("class io.philo.shop.exception.ItemNotFoundForOrderException");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(ItemNotFoundForOrderException.class);
    }

    @Test
    void throwIfConfigured_doesNothingWhenUnknownType() {
        ManualItemServiceExceptionTrigger injector = createInjector("UNKNOWN_TYPE");

        assertThatCode(injector::throwIfConfigured)
            .doesNotThrowAnyException();
    }

    @Test
    void throwIfConfigured_doesNothingWhenBlank() {
        ManualItemServiceExceptionTrigger injector = createInjector(" ");

        assertThatCode(injector::throwIfConfigured)
            .doesNotThrowAnyException();
    }

    private static ManualItemServiceExceptionTrigger createInjector(String configuredExceptionType) {
        ManualItemServiceExceptionTrigger injector = new ManualItemServiceExceptionTrigger();
        ReflectionTestUtils.setField(injector, "configuredExceptionType", configuredExceptionType);
        return injector;
    }
}
