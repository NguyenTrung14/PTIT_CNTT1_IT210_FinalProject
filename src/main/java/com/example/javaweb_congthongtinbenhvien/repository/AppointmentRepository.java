package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateAscStartTimeAsc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long doctorId,
            AppointmentStatus status
    );

    List<Appointment> findByStatusOrderByAppointmentDateAscStartTimeAsc(AppointmentStatus status);

    boolean existsByDoctorIdAndAppointmentDateAndStartTimeAndEndTimeAndStatusIn(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Collection<AppointmentStatus> statuses
    );

    @Query("""
            select count(a) > 0
            from Appointment a
            where a.doctor.id = :doctorId
              and a.appointmentDate = :appointmentDate
              and a.status in :statuses
              and a.startTime < :endTime
              and a.endTime > :startTime
            """)
    boolean existsOverlapAppointment(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );
}