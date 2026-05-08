package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicalTestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "medical_tests",
        indexes = {
                @Index(name = "idx_medical_tests_medical_record_id", columnList = "medical_record_id"),
                @Index(name = "idx_medical_tests_test_type_id", columnList = "test_type_id"),
                @Index(name = "idx_medical_tests_patient_id", columnList = "patient_id"),
                @Index(name = "idx_medical_tests_doctor_id", columnList = "doctor_id"),
                @Index(name = "idx_medical_tests_status", columnList = "status")
        }
)
public class MedicalTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "test_type_id", nullable = false)
    private TestType testType;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MedicalTestStatus status = MedicalTestStatus.ORDERED;

    @Column(name = "test_reason", columnDefinition = "TEXT")
    private String testReason;

    @Column(name = "test_result", columnDefinition = "TEXT")
    private String testResult;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (orderedAt == null) {
            orderedAt = now;
        }

        if (status == null) {
            status = MedicalTestStatus.ORDERED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}