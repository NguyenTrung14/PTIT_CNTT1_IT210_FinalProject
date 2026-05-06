package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.entity.Appointment;

public interface AsyncEmailService {

    void sendAppointmentPaidEmail(Appointment appointment);
}
