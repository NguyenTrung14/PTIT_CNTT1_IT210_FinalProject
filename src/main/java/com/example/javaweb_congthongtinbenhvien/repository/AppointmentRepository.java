package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    /*
     * Chống trùng lịch:
     * Nếu lịch mới có khoảng giờ giao nhau với lịch cũ của cùng bác sĩ
     * và lịch cũ đang ở trạng thái còn hiệu lực thì không cho đặt.
     *
     * Điều kiện overlap:
     * startTime < oldEndTime AND endTime > oldStartTime
     */
    @Query("""
            select count(a) > 0
            from Appointment a
            where a.doctor.id = :doctorId
              and a.appointmentDate = :appointmentDate
              and a.status in :statuses
              and :startTime < a.endTime
              and :endTime > a.startTime
            """)
    boolean existsOverlapAppointment(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Collection<AppointmentStatus> statuses
    );
}