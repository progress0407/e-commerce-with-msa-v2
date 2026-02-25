package io.philo.shop.domain;

import io.philo.shop.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "order_line_item")
@Getter
public class OrderLineItemEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private Long itemId;

    @Column(nullable = false)
    private int itemRawAmount;

    @Column(nullable = false)
    private int itemDiscountedAmount;

    @Column(nullable = false)
    private int orderedQuantity;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderEntity orderEntity;

    protected OrderLineItemEntity() {
        this.itemId = 0L;
        this.itemRawAmount = 0;
        this.itemDiscountedAmount = 0;
        this.orderedQuantity = 0;
    }

    public OrderLineItemEntity(Long itemId, int itemRawAmount, int itemDiscountedAmount, int orderedQuantity) {
        this.itemId = itemId;
        this.itemRawAmount = itemRawAmount;
        this.itemDiscountedAmount = itemDiscountedAmount;
        this.orderedQuantity = orderedQuantity;
    }

    public void mapOrder(OrderEntity orderEntity) {
        this.orderEntity = orderEntity;
    }

    public static OrderLineItemEntity empty() {
        return new OrderLineItemEntity();
    }
}
