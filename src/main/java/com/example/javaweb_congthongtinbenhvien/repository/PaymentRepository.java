package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Payment;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByTransactionCode(String transactionCode);
}