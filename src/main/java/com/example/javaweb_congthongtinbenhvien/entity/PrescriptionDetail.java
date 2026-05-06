package com.example.javaweb_congthongtinbenhvien.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "prescription_details",
        indexes = {
                @Index(name = "idx_prescription_details_prescription_id", columnList = "prescription_id"),
                @Index(name = "idx_prescription_details_medicine_id", columnList = "medicine_id")
        }
)
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thuộc đơn thuốc nào
    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    // Thuốc được kê
    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity = 1;

    // Ví dụ: 1 viên/lần, ngày 2 lần
    @Column(length = 255)
    private String dosage;

    @Column(name = "usage_instruction", length = 255)
    private String usageInstruction;

    @PrePersist
    public void prePersist() {
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }
    }
}