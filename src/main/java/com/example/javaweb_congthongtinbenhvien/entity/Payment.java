package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentMethod;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payments_patient_id", columnList = "patient_id"),
                @Index(name = "idx_payments_appointment_id", columnList = "appointment_id"),
                @Index(name = "idx_payments_status", columnList = "status"),
                @Index(name = "idx_payments_transaction_code", columnList = "transaction_code")
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method = PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_code", unique = true, length = 100)
    private String transactionCode;

    @Column(length = 255)
    private String description;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        if (method == null) {
            method = PaymentMethod.CASH;
        }

        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}