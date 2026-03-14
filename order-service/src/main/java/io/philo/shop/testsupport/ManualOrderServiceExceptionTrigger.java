package io.philo.shop.testsupport;

import static io.philo.shop.testsupport.ManualOrderServiceExceptionTrigger.FailureType.from;

import org.springframework.beans.factory.annotation.Value;

import io.philo.shop.exception.OrderNotFoundForCancelException;

@ManualExceptionTrigger
public class ManualOrderServiceExceptionTrigger {

    @Value("${app.manual.exception.type:}")
    private String configuredExceptionType;

    public void throwIfConfigured() {
        FailureType failureType = from(configuredExceptionType);
        switch (failureType) {
            case ORDER_NOT_FOUND_EXCEPTION -> throw new OrderNotFoundForCancelException(-9999L);
            case NONE -> {
                // 설정 값이 비어 있거나 알 수 없는 경우 아무 동작도 하지 않음.
            }
        }
    }

    enum FailureType {
        NONE,
        ORDER_NOT_FOUND_EXCEPTION;

        static FailureType from(String rawValue) {
            if (rawValue == null) {
                return NONE;
            }
            String normalized = rawValue.trim();
            if (normalized.isEmpty() || normalized.equalsIgnoreCase("none")) {
                return NONE;
            }

            return switch (normalized) {
                case "OrderNotFoundForCancelException",
                     "io.philo.shop.exception.OrderNotFoundForCancelException" -> ORDER_NOT_FOUND_EXCEPTION;
                default -> NONE;
            };
        }
    }
}
