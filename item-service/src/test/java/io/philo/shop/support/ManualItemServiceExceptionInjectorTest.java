package io.philo.shop.support;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.philo.shop.exception.InsufficientStockException;
import io.philo.shop.exception.InvalidOrderQuantityException;
import io.philo.shop.exception.ItemNotFoundForOrderException;

class ManualItemServiceExceptionInjectorTest {

    @Test
    void throwIfConfigured_throwsInvalidOrderQuantityException() {
        ManualItemServiceExceptionInjector injector = createInjector("InvalidOrderQuantityException");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(InvalidOrderQuantityException.class);
    }

    @Test
    void throwIfConfigured_throwsInsufficientStockException() {
        ManualItemServiceExceptionInjector injector = createInjector("INSUFFICIENT_STOCK");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void throwIfConfigured_throwsItemNotFoundException() {
        ManualItemServiceExceptionInjector injector = createInjector("ItemNotFoundForOrderException");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(ItemNotFoundForOrderException.class);
    }

    @Test
    void throwIfConfigured_acceptsLegacyClassToStringFormat() {
        ManualItemServiceExceptionInjector injector =
            createInjector("class io.philo.shop.exception.ItemNotFoundForOrderException");

        assertThatThrownBy(injector::throwIfConfigured)
            .isInstanceOf(ItemNotFoundForOrderException.class);
    }

    @Test
    void throwIfConfigured_doesNothingWhenUnknownType() {
        ManualItemServiceExceptionInjector injector = createInjector("UNKNOWN_TYPE");

        assertThatCode(injector::throwIfConfigured)
            .doesNotThrowAnyException();
    }

    @Test
    void throwIfConfigured_doesNothingWhenBlank() {
        ManualItemServiceExceptionInjector injector = createInjector(" ");

        assertThatCode(injector::throwIfConfigured)
            .doesNotThrowAnyException();
    }

    private static ManualItemServiceExceptionInjector createInjector(String configuredExceptionType) {
        ManualItemServiceExceptionInjector injector = new ManualItemServiceExceptionInjector();
        ReflectionTestUtils.setField(injector, "configuredExceptionType", configuredExceptionType);
        return injector;
    }
}
