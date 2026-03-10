package io.philo.shop;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderLine> orderLines
) {

    public record OrderLine(
            Long itemId,
            int quantity
    ) {
    }
}
