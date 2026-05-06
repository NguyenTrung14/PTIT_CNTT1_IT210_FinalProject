package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.service.AsyncEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncEmailServiceImpl implements AsyncEmailService {

    private static final Logger log = LoggerFactory.getLogger(AsyncEmailServiceImpl.class);

    @Async
    @Override
    public void sendAppointmentPaidEmail(Appointment appointment) {
        log.info(
                "ASYNC EMAIL | To: {} | Subject: Xac nhan lich kham #{} | Bac si: {} | Ngay: {} {}-{}",
                appointment.getPatient().getEmail(),
                appointment.getId(),
                appointment.getDoctor().getUser().getFullName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );
    }
}
