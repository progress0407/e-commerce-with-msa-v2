package io.philo.shop.testsupport;

import static io.philo.shop.testsupport.ManualItemServiceExceptionTrigger.FailureType.*;

import org.springframework.beans.factory.annotation.Value;

import io.philo.shop.exception.InsufficientStockException;
import io.philo.shop.exception.InvalidOrderQuantityException;
import io.philo.shop.exception.ItemNotFoundForOrderException;

@ManualExceptionTrigger
public class ManualItemServiceExceptionTrigger {

	@Value("${app.manual.exception.type:}")
	private String configuredExceptionType;

	public void throwIfConfigured() {
		FailureType failureType = from(configuredExceptionType);
		switch (failureType) {
			case INVALID_ORDER_QUANTITY -> throw new InvalidOrderQuantityException(0L, -9999);
			case INSUFFICIENT_STOCK -> throw new InsufficientStockException(0L, -9999, -9999);
			case ITEM_NOT_FOUND -> throw new ItemNotFoundForOrderException(0L);
			case NONE -> {
				// 설정 값이 비어 있거나 그 외의 경우 아무 동작하지 않음
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
				case "InvalidOrderQuantityException",
					 "io.philo.shop.exception.InvalidOrderQuantityException" -> INVALID_ORDER_QUANTITY;
				case "InsufficientStockException",
					 "io.philo.shop.exception.InsufficientStockException" -> INSUFFICIENT_STOCK;
				case "ItemNotFoundForOrderException",
					 "io.philo.shop.exception.ItemNotFoundForOrderException" -> ITEM_NOT_FOUND;
				default -> NONE;
			};
		}
	}
}
