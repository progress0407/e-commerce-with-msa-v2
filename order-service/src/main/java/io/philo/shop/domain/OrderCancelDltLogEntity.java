package io.philo.shop.domain;

import io.philo.shop.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "order_cancel_dlt_log")
@Getter
public class OrderCancelDltLogEntity extends BaseEntity {

    @Column(nullable = false)
    private Long orderId;

    @Column
    private Long itemId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false)
    private String dltTopic;

    @Column(nullable = false)
    private int dltPartition;

    @Column(nullable = false)
    private long dltOffset;

    @Column(nullable = false)
    private boolean reprocessAttempted = false;

    @Column(nullable = false)
    private boolean reprocessSucceeded = false;

    @Column(length = 1000)
    private String reprocessErrorMessage;

    protected OrderCancelDltLogEntity() {
    }

    public OrderCancelDltLogEntity(Long orderId, Long itemId, String reason, String dltTopic, int dltPartition, long dltOffset) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.reason = reason;
        this.dltTopic = dltTopic;
        this.dltPartition = dltPartition;
        this.dltOffset = dltOffset;
    }

    public void markReprocessSucceeded() {
        this.reprocessAttempted = true;
        this.reprocessSucceeded = true;
        this.reprocessErrorMessage = null;
    }

    public void markReprocessFailed(String errorMessage) {
        this.reprocessAttempted = true;
        this.reprocessSucceeded = false;
        this.reprocessErrorMessage = errorMessage;
    }
}
