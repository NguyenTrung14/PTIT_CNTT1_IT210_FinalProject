package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<MedicalRecord> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    /*
     * Dùng cho CORE-07:
     * Bệnh nhân xem lịch sử bệnh án đầy đủ:
     * - Bác sĩ
     * - Lịch khám
     * - Đơn thuốc
     * - Chi tiết thuốc
     *
     * Nếu không join fetch, view Thymeleaf dễ bị LazyInitializationException.
     */
    @Query("""
            select distinct mr
            from MedicalRecord mr
            left join fetch mr.doctor d
            left join fetch d.user
            left join fetch mr.appointment a
            left join fetch mr.prescription p
            left join fetch p.details pd
            left join fetch pd.medicine
            where mr.patient.id = :patientId
            order by mr.createdAt desc
            """)
    List<MedicalRecord> findFullHistoryByPatientId(Long patientId);
}