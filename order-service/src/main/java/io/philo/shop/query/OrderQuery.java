package io.philo.shop.query;

import java.util.List;

import org.springframework.stereotype.Component;

import io.philo.shop.domain.OrderEntity;
import io.philo.shop.dto.web.OrderDetailResponse;
import io.philo.shop.dto.web.OrderListResponse;
import io.philo.shop.dto.web.OrderListResponses;
import io.philo.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderQuery {

    private final OrderRepository orderRepository;

    public OrderListResponses list() {
        List<OrderEntity> savedItems = orderRepository.findAll();
        return convertListDtos(savedItems);
    }

    public OrderDetailResponse detail(Long id) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
        return new OrderDetailResponse(entity);
    }

    private OrderListResponses convertListDtos(List<OrderEntity> savedItems) {
        List<OrderListResponse> dtos = savedItems.stream()
                .map(OrderListResponse::new)
                .toList();
        return new OrderListResponses(dtos);
    }
}
