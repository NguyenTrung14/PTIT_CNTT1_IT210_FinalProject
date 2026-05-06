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
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method = PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @PrePersist
    public void prePersist() {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        if (method == null) {
            method = PaymentMethod.CASH;
        }

        if (status == null) {
            status = PaymentStatus.PENDING;
        }

        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
    }
}
