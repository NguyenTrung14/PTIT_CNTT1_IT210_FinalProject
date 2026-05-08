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