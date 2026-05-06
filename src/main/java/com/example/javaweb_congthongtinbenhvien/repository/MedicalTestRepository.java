package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.MedicalTest;
import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicalTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalTestRepository extends JpaRepository<MedicalTest, Long> {

    List<MedicalTest> findByMedicalRecordId(Long medicalRecordId);

    List<MedicalTest> findByPatientIdOrderByOrderedAtDesc(Long patientId);

    List<MedicalTest> findByDoctorIdOrderByOrderedAtDesc(Long doctorId);

    List<MedicalTest> findByStatusOrderByOrderedAtDesc(MedicalTestStatus status);
}