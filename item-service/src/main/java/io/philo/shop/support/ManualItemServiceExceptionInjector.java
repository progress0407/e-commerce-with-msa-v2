package io.philo.shop.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.philo.shop.exception.InsufficientStockException;
import io.philo.shop.exception.InvalidOrderQuantityException;
import io.philo.shop.exception.ItemNotFoundForOrderException;

@Component
public class ManualItemServiceExceptionInjector {

    @Value("${app.manual.exception.type:}")
    private String configuredExceptionType;

    public void throwIfConfigured() {
        FailureType failureType = FailureType.from(configuredExceptionType);
        switch (failureType) {
            case INVALID_ORDER_QUANTITY -> throw new InvalidOrderQuantityException(0L, -9999);
            case INSUFFICIENT_STOCK -> throw new InsufficientStockException(0L, -9999, -9999);
            case ITEM_NOT_FOUND -> throw new ItemNotFoundForOrderException(0L);
            case NONE -> {
                // Explicitly no-op for empty/unknown configuration values.
            }
        }
    }

    enum FailureType {
        NONE,
        INVALID_ORDER_QUANTITY,
        INSUFFICIENT_STOCK,
        ITEM_NOT_FOUND;

        static FailureType from(String rawValue) {
            if (rawValue == null) {
                return NONE;
            }
            String normalized = rawValue.trim();
            if (normalized.isEmpty() || normalized.equalsIgnoreCase("none")) {
                return NONE;
            }
            return switch (normalized) {
                case "INVALID_ORDER_QUANTITY",
                     "InvalidOrderQuantityException",
                     "io.philo.shop.exception.InvalidOrderQuantityException",
                     "class io.philo.shop.exception.InvalidOrderQuantityException" -> INVALID_ORDER_QUANTITY;
                case "INSUFFICIENT_STOCK",
                     "InsufficientStockException",
                     "io.philo.shop.exception.InsufficientStockException",
                     "class io.philo.shop.exception.InsufficientStockException" -> INSUFFICIENT_STOCK;
                case "ITEM_NOT_FOUND",
                     "ItemNotFoundForOrderException",
                     "io.philo.shop.exception.ItemNotFoundForOrderException",
                     "class io.philo.shop.exception.ItemNotFoundForOrderException" -> ITEM_NOT_FOUND;
                default -> NONE;
            };
        }
    }
}
