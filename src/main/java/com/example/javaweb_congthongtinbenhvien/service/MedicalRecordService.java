package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.dto.MedicalRecordRequest;
import com.example.javaweb_congthongtinbenhvien.entity.MedicalRecord;

import java.util.List;

public interface MedicalRecordService {

    MedicalRecord createMedicalRecord(MedicalRecordRequest request);

    MedicalRecord findById(Long id);

    List<MedicalRecord> findByPatientId(Long patientId);

    List<MedicalRecord> findByDoctorId(Long doctorId);
}