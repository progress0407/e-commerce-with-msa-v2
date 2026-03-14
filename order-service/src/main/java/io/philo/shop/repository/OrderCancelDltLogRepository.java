package io.philo.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.philo.shop.domain.OrderCancelDltLogEntity;

public interface OrderCancelDltLogRepository extends JpaRepository<OrderCancelDltLogEntity, Long> {
}
