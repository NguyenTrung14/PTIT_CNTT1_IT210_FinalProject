package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.entity.Prescription;

import java.util.List;

public interface PrescriptionService {

    List<Prescription> findWaitingDispense();

    List<Prescription> findByDoctorUserId(Long doctorUserId);

    Prescription findById(Long id);

    Prescription findByIdForDoctor(Long id, Long doctorUserId);

    void dispensePrescription(Long prescriptionId, Long userId);
}
