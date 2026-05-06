package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.Payment;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus;
import com.example.javaweb_congthongtinbenhvien.repository.AppointmentRepository;
import com.example.javaweb_congthongtinbenhvien.repository.PaymentRepository;
import com.example.javaweb_congthongtinbenhvien.service.AsyncEmailService;
import com.example.javaweb_congthongtinbenhvien.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final AsyncEmailService asyncEmailService;

    @Override
    public Payment findByAppointmentId(Long appointmentId, Long patientId) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay giao dich thanh toan"));

        if (!payment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Ban khong co quyen xem giao dich nay");
        }

        return payment;
    }

    @Override
    @Transactional
    public Payment confirmPayment(Long appointmentId, Long patientId) {
        Payment payment = findByAppointmentId(appointmentId, patientId);
        Appointment appointment = payment.getAppointment();

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Lich kham da bi huy");
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return payment;
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Giao dich khong o trang thai cho thanh toan");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionCode("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        appointment.setStatus(AppointmentStatus.WAITING);
        appointmentRepository.save(appointment);

        Payment savedPayment = paymentRepository.save(payment);
        asyncEmailService.sendAppointmentPaidEmail(appointment);

        return savedPayment;
    }

    @Override
    public BigDecimal totalPaidRevenue() {
        return paymentRepository.totalPaidRevenue();
    }

    @Override
    public List<Object[]> revenueByMonth() {
        return paymentRepository.revenueByMonth();
    }
}
