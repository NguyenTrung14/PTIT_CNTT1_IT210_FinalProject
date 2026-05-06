package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.TestType;
import com.example.javaweb_congthongtinbenhvien.entity.enums.CommonStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestTypeRepository extends JpaRepository<TestType, Long> {

    Optional<TestType> findByName(String name);

    boolean existsByName(String name);

    List<TestType> findByStatus(CommonStatus status);
}