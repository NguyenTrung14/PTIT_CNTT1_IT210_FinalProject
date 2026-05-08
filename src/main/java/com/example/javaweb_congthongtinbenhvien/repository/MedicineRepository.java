package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByName(String name);

    Optional<Medicine> findByNameIgnoreCase(String name);

    @Query("""
            select m
            from Medicine m
            where lower(trim(m.name)) = lower(trim(:name))
            """)
    Optional<Medicine> findByNormalizedName(@Param("name") String name);

    Optional<Medicine> findFirstByStatusOrderByIdAsc(MedicineStatus status);

    boolean existsByName(String name);

    List<Medicine> findByStatus(MedicineStatus status);

    List<Medicine> findByNameContainingIgnoreCaseAndStatus(String name, MedicineStatus status);

    Page<Medicine> findByStatus(MedicineStatus status, Pageable pageable);

    Page<Medicine> findByNameContainingIgnoreCaseAndStatus(String name, MedicineStatus status, Pageable pageable);

    @Modifying
    @Query(value = """
            update medicines m
            join prescription_details pd on pd.medicine_id = m.id
            set m.stock_quantity = m.stock_quantity - pd.quantity
            where pd.prescription_id = :prescriptionId
            """, nativeQuery = true)
    int deductStockByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
}
