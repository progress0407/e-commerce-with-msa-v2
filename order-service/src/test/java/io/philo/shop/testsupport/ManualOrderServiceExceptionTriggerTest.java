package io.philo.shop.testsupport;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ManualOrderServiceExceptionTriggerTest {

    @Test
    void throwIfConfigured_throwsIllegalArgumentException() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("ILLEGAL_ARGUMENT");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwIfConfigured_throwsIllegalStateException() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("IllegalStateException");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void throwIfConfigured_throwsRuntimeException() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("java.lang.RuntimeException");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void throwIfConfigured_acceptsLegacyClassToStringFormat() {
        ManualOrderServiceExceptionTrigger trigger = createTrigger("class java.lang.IllegalArgumentException");

        assertThatThrownBy(trigger::throwIfConfigured)
            .isInstanceOf(IllegalArgumentException.class);
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
