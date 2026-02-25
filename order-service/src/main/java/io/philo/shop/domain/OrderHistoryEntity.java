package io.philo.shop.domain;

import io.philo.shop.constant.OrderStatus;
import io.philo.shop.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "order_history")
@Getter
public class OrderHistoryEntity extends BaseEntity {

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderEntity orderEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    protected OrderHistoryEntity() {
    }

    public OrderHistoryEntity(OrderEntity orderEntity) {
        this(orderEntity, OrderStatus.PENDING);
    }

    public OrderHistoryEntity(OrderEntity orderEntity, OrderStatus orderStatus) {
        this.orderEntity = orderEntity;
        this.orderStatus = orderStatus;
    }
}
