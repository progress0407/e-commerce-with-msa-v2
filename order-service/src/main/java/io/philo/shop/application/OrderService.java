package io.philo.shop.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.philo.shop.OrderCreatedEvent;
import io.philo.shop.constant.OrderStatus;
import io.philo.shop.domain.OrderEntity;
import io.philo.shop.domain.OrderLineItemEntity;
import io.philo.shop.dto.web.OrderLineRequestDto;
import io.philo.shop.exception.OrderNotFoundForCancelException;
import io.philo.shop.messaging.OrderEventProducer;
import io.philo.shop.repository.OrderRepository;
import io.philo.shop.testsupport.ManualOrderServiceExceptionTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final ManualOrderServiceExceptionTrigger manualOrderServiceExceptionTrigger;

    @Transactional
    public Long order(List<OrderLineRequestDto> orderLineDtos) {

		var orderEntity = orderRepository.save(createOrder(orderLineDtos));
		var orderCreatedEvent = createOrderCreatedEvent(orderEntity);
        orderEventProducer.publishOrderCreated(orderCreatedEvent);

        return orderEntity.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundForCancelException(orderId));

        if (OrderStatus.CANCEL == orderEntity.getOrderStatus()) {
            log.info("이미 주문이 취소되었습니다.");
            return;
        }

        orderEntity.completeToCanceled();
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
