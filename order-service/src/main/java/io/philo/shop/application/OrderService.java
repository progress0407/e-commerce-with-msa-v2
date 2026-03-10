package io.philo.shop.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.philo.OrderCreatedEvent;
import io.philo.shop.domain.OrderEntity;
import io.philo.shop.domain.OrderLineItemEntity;
import io.philo.shop.dto.web.OrderLineRequestDto;
import io.philo.shop.messaging.OrderEventProducer;
import io.philo.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public Long order(List<OrderLineRequestDto> orderLineDtos) {

        OrderEntity orderEntity = createOrder(orderLineDtos);
        // orderRepository.save(orderEntity);
        orderEventProducer.publishOrderCreated(createOrderCreatedEvent(orderEntity));

        return orderEntity.getId();
    }

    private OrderEntity createOrder(List<OrderLineRequestDto> orderLineDtos) {
        List<OrderLineItemEntity> orderItems = toEntities(orderLineDtos);
        return new OrderEntity(orderItems);
    }

    private List<OrderLineItemEntity> toEntities(List<OrderLineRequestDto> orderLineDtos) {
        List<OrderLineItemEntity> entities = new ArrayList<>();

        if (orderLineDtos == null) {
            return entities;
        }

        for (OrderLineRequestDto dto : orderLineDtos) {
            entities.add(createOrderLine(dto));
        }

        return entities;
    }

    private OrderLineItemEntity createOrderLine(OrderLineRequestDto dto) {
        OrderLineItemEntity orderLineItemEntity = new OrderLineItemEntity(
                dto.itemId(),
                dto.itemAmount(),
                dto.itemDiscountedAmount(),
                dto.itemQuantity()
        );

        return orderLineItemEntity;
    }

    private OrderCreatedEvent createOrderCreatedEvent(OrderEntity orderEntity) {
        List<OrderCreatedEvent.OrderLine> orderLines = orderEntity.getOrderLineItemEntities().stream()
                .map(orderLine -> new OrderCreatedEvent.OrderLine(orderLine.getItemId(), orderLine.getOrderedQuantity()))
                .toList();

        return new OrderCreatedEvent(orderEntity.getId(), orderLines);
    }
}
