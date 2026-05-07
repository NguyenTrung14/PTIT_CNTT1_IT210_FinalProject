package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicineStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByName(String name);

    Optional<Medicine> findFirstByStatusOrderByIdAsc(MedicineStatus status);

    boolean existsByName(String name);

    List<Medicine> findByStatus(MedicineStatus status);

    List<Medicine> findByNameContainingIgnoreCaseAndStatus(String name, MedicineStatus status);
}
