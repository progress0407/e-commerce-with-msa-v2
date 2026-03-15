package io.philo.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.philo.shop.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
