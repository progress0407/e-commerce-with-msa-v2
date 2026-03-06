package io.philo.shop.query;

import java.util.List;

import org.springframework.stereotype.Component;

import io.philo.shop.domain.OrderEntity;
import io.philo.shop.dto.web.OrderListResponse;
import io.philo.shop.dto.web.OrderListResponses;
import io.philo.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderQuery {

    private final OrderRepository orderRepository;

    public List<OrderEntity> list() {
        return orderRepository.findAll();

    }

    public OrderEntity detail(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
    }
}
