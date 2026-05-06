package com.example.javaweb_congthongtinbenhvien.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false, columnDefinition = "text")
    private String symptoms;

    @Column(nullable = false, columnDefinition = "text")
    private String diagnosis;

    @Column(name = "treatment_plan", columnDefinition = "text")
    private String treatmentPlan;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    private Prescription prescription;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}