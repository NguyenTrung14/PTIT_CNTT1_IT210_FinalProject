package com.example.javaweb_congthongtinbenhvien.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MedicalRecordRequest {

    private Long appointmentId;

    private String symptoms;

    private String diagnosis;

    private String treatmentPlan;

    private String note;

    private List<PrescriptionDetailRequest> prescriptionDetails = new ArrayList<>();
}