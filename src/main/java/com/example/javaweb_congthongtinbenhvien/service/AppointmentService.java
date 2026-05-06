package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.dto.AppointmentRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment bookAppointment(AppointmentRequest request);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findWaitingByDoctorId(Long doctorId);

    Appointment findById(Long id);

    void cancelAppointment(Long appointmentId, Long patientId, String cancelReason);
}