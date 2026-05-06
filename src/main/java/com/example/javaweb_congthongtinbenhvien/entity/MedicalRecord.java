package com.example.javaweb_congthongtinbenhvien.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "medical_records",
        indexes = {
                @Index(name = "idx_medical_records_patient_id", columnList = "patient_id"),
                @Index(name = "idx_medical_records_doctor_id", columnList = "doctor_id"),
                @Index(name = "idx_medical_records_appointment_id", columnList = "appointment_id")
        }
)
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mỗi lịch khám hoàn thành có đúng 1 bệnh án
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    // Lưu trực tiếp patient để tra cứu lịch sử nhanh
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Bác sĩ khám
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Một bệnh án có một đơn thuốc
    @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    private Prescription prescription;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalTest> medicalTests = new ArrayList<>();


    public void addMedicalTest(MedicalTest medicalTest) {
        medicalTests.add(medicalTest);
        medicalTest.setMedicalRecord(this);
    }

    public void removeMedicalTest(MedicalTest medicalTest) {
        medicalTests.remove(medicalTest);
        medicalTest.setMedicalRecord(null);
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}