package io.philo.shop.entity;

import java.time.LocalDateTime;

import io.philo.shop.constant.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "payment")
@Getter
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, unique = true, length = 100)
    private String paymentId;

    @Column(nullable = false, length = 100)
    private String orderName;

    @Column(nullable = false, length = 100)
    private String customer;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private int resultCode;

    @Column(nullable = false, length = 500)
    private String resultMessage;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    protected PaymentEntity() {
    }

    public PaymentEntity(
            Long orderId,
            String paymentId,
            String orderName,
            String customer,
            int totalAmount,
            String currency,
            PaymentStatus paymentStatus,
            int resultCode,
            String resultMessage
    ) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.orderName = orderName;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.paymentStatus = paymentStatus;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.processedAt = LocalDateTime.now();
    }
}
