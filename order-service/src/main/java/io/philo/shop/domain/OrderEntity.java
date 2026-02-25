package io.philo.shop.domain;

import java.util.ArrayList;
import java.util.List;

import io.philo.shop.constant.OrderStatus;
import io.philo.shop.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "orders")
@Getter
public class OrderEntity extends BaseEntity {

    @Column(nullable = false)
    private Long requesterId = 0L;

    @Column(nullable = false)
    private int totalOrderAmount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<OrderLineItemEntity> orderLineItemEntities = new ArrayList<>();

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<OrderHistoryEntity> orderHistories = new ArrayList<>();

    protected OrderEntity() {
    }

    public OrderEntity(List<OrderLineItemEntity> orderLineItemEntities) {
        if (orderLineItemEntities != null) {
            this.orderLineItemEntities.addAll(orderLineItemEntities);
        }
        mapOrder(this.orderLineItemEntities);
        this.totalOrderAmount = this.orderLineItemEntities.stream()
                .mapToInt(orderLine -> orderLine.getItemDiscountedAmount() * orderLine.getOrderedQuantity())
                .sum();
        this.orderHistories.add(new OrderHistoryEntity(this, OrderStatus.PENDING));
    }

    private void mapOrder(List<OrderLineItemEntity> orderLineItemEntities) {
        for (OrderLineItemEntity orderItem : orderLineItemEntities) {
            orderItem.mapOrder(this);
        }
    }

    public void completeToSuccess() {
        this.orderStatus = OrderStatus.SUCCESS;
        this.orderHistories.add(new OrderHistoryEntity(this, OrderStatus.SUCCESS));
    }

    public void completeToFail() {
        this.orderStatus = OrderStatus.FAIL;
        this.orderHistories.add(new OrderHistoryEntity(this, OrderStatus.FAIL));
    }

    public void completeToCanceled() {
        this.orderStatus = OrderStatus.CANCEL;
        this.orderHistories.add(new OrderHistoryEntity(this, OrderStatus.CANCEL));
    }

    public boolean isSuccess() {
        return this.orderStatus == OrderStatus.SUCCESS;
    }

    public boolean isFail() {
        return this.orderStatus == OrderStatus.FAIL;
    }

    public static OrderEntity empty() {
        return new OrderEntity();
    }
}
