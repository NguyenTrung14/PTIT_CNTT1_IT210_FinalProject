package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    Payment findByAppointmentId(Long appointmentId, Long patientId);

    Payment confirmPayment(Long appointmentId, Long patientId);

    BigDecimal totalPaidRevenue();

    List<Object[]> revenueByMonth();
}
