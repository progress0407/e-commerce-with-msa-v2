package io.philo.shop.messaging;

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
