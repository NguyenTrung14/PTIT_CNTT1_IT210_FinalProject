package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Prescription;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByMedicalRecordId(Long medicalRecordId);

    List<Prescription> findByStatusOrderByCreatedAtAsc(PrescriptionStatus status);

    
    @Query("""
            select distinct p
            from Prescription p
            left join fetch p.medicalRecord mr
            left join fetch mr.patient
            left join fetch mr.doctor d
            left join fetch d.user
            left join fetch p.details pd
            left join fetch pd.medicine
            where p.status = :status
            order by p.createdAt asc
            """)
    List<Prescription> findFullByStatusOrderByCreatedAtAsc(PrescriptionStatus status);

    
    @Query("""
            select distinct p
            from Prescription p
            left join fetch p.medicalRecord mr
            left join fetch mr.patient
            left join fetch mr.doctor d
            left join fetch d.user
            left join fetch p.details pd
            left join fetch pd.medicine
            where p.id = :id
            """)
    Optional<Prescription> findFullById(Long id);
}