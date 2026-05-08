package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.repository.AppointmentRepository;
import com.example.javaweb_congthongtinbenhvien.repository.PaymentRepository;
import com.example.javaweb_congthongtinbenhvien.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public BigDecimal totalPaidRevenue() {
        return paymentRepository.totalPaidRevenue();
    }

    @Override
    public List<Object[]> revenueByMonth() {
        return paymentRepository.revenueByMonth();
    }

    @Override
    public List<Object[]> topDoctors() {
        return appointmentRepository.findTopDoctorsByAppointments(PageRequest.of(0, 5));
    }
}
