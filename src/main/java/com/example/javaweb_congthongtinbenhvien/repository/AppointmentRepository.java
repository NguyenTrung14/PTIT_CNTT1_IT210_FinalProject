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

    @Query(value = """
            select *
            from appointments a
            where a.patient_id = :patientId
            order by
              a.appointment_date asc,
              a.start_time asc
            """, nativeQuery = true)
    List<Appointment> findByPatientIdOrderByAppointmentTimeAsc(@Param("patientId") Long patientId);

    @Query(value = """
            select *
            from appointments a
            where a.doctor_id = :doctorId
            order by
              a.appointment_date asc,
              a.start_time asc
            """, nativeQuery = true)
    List<Appointment> findByDoctorIdOrderByAppointmentTimeAsc(@Param("doctorId") Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long doctorId,
            AppointmentStatus status
    );

    @Query(value = """
            select *
            from appointments a
            where a.doctor_id = :doctorId
              and a.status = :status
            order by
              a.appointment_date asc,
              a.start_time asc
            """, nativeQuery = true)
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentTimeAsc(
            @Param("doctorId") Long doctorId,
            @Param("status") String status
    );

    List<Appointment> findByStatusOrderByAppointmentDateAscStartTimeAsc(AppointmentStatus status);

    @Query("""
            select a
            from Appointment a
            left join fetch a.payment
            where a.status in :statuses
              and (
                    a.appointmentDate < :currentDate
                    or (a.appointmentDate = :currentDate and a.endTime < :currentTime)
              )
            """)
    List<Appointment> findOverdueAppointments(
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime,
            @Param("statuses") Collection<AppointmentStatus> statuses
    );

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

    @Query(value = """
            select count(*)
            from appointments a
            where a.patient_id = :patientId
              and a.appointment_date = :appointmentDate
              and a.status in (:statuses)
              and abs(timestampdiff(
                    minute,
                    timestamp(a.appointment_date, a.start_time),
                    timestamp(:appointmentDate, :startTime)
              )) < :gapMinutes
            """, nativeQuery = true)
    long countPatientScheduleConflicts(
            @Param("patientId") Long patientId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("gapMinutes") Long gapMinutes,
            @Param("statuses") Collection<String> statuses
    );

    @Query("""
            select d.user.fullName, count(a.id)
            from Appointment a
            join a.doctor d
            where d.status = com.example.javaweb_congthongtinbenhvien.entity.enums.CommonStatus.ACTIVE
              and a.status <> com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus.CANCELLED
            group by d.id, d.user.fullName
            order by count(a.id) desc, d.user.fullName asc
            """)
    List<Object[]> findTopDoctorsByAppointments(Pageable pageable);
}
