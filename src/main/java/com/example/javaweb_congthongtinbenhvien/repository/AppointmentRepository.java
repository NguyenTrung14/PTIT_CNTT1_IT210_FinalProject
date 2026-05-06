package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Pageable;
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
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

    @Query("""
            select d.user.fullName, count(a.id)
            from Appointment a
            join a.doctor d
            where a.status = com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus.COMPLETED
            group by d.id, d.user.fullName
            order by count(a.id) desc
            """)
    List<Object[]> findTopDoctorsByCompletedAppointments(Pageable pageable);
}
