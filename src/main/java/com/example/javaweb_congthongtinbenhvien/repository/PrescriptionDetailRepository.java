package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {

    List<PrescriptionDetail> findByPrescriptionId(Long prescriptionId);

    @Query("""
            select pd
            from PrescriptionDetail pd
            left join fetch pd.medicine
            where pd.prescription.id = :prescriptionId
            order by pd.id asc
            """)
    List<PrescriptionDetail> findFullByPrescriptionId(Long prescriptionId);

    List<PrescriptionDetail> findByMedicineId(Long medicineId);

    boolean existsByPrescriptionId(Long prescriptionId);

    @Query(value = """
            select m.name
            from prescription_details pd
            join medicines m on m.id = pd.medicine_id
            where pd.prescription_id = :prescriptionId
              and (
                    pd.quantity is null
                    or pd.quantity <= 0
                    or m.stock_quantity is null
                    or m.stock_quantity < pd.quantity
              )
            limit 1
            """, nativeQuery = true)
    Optional<String> findFirstInvalidOrInsufficientStockMedicineName(
            @Param("prescriptionId") Long prescriptionId
    );
}
