package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "prescriptions",
        indexes = {
                @Index(name = "idx_prescriptions_medical_record_id", columnList = "medical_record_id"),
                @Index(name = "idx_prescriptions_status", columnList = "status")
        }
)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "medical_record_id", nullable = false, unique = true)
    private MedicalRecord medicalRecord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PrescriptionStatus status = PrescriptionStatus.WAITING_DISPENSE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "dispensed_at")
    private LocalDateTime dispensedAt;

    @ManyToOne
    @JoinColumn(name = "dispensed_by")
    private User dispensedBy;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionDetail> details = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = PrescriptionStatus.WAITING_DISPENSE;
        }
    }

    public void addDetail(PrescriptionDetail detail) {
        details.add(detail);
        detail.setPrescription(this);
    }

    public void removeDetail(PrescriptionDetail detail) {
        details.remove(detail);
        detail.setPrescription(null);
    }
}