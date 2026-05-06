package com.example.javaweb_congthongtinbenhvien.dto;

import com.example.javaweb_congthongtinbenhvien.entity.enums.PrescriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class PatientHistoryResponse {

    private Long medicalRecordId;

    private LocalDate appointmentDate;

    private LocalTime startTime;

    private String doctorName;

    private String specialtyName;

    private String symptoms;

    private String diagnosis;

    private String treatmentPlan;

    private PrescriptionStatus prescriptionStatus;
}