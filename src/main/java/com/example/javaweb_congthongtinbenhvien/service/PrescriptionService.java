package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.entity.Prescription;

import java.util.List;

public interface PrescriptionService {

    List<Prescription> findWaitingDispense();

    Prescription findById(Long id);

    void dispensePrescription(Long prescriptionId, Long userId);
}