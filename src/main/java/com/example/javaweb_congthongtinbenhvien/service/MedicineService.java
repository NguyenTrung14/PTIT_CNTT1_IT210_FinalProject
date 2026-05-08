package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.dto.MedicineRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MedicineService {

    List<Medicine> findAllActive();

    List<Medicine> search(String keyword);

    Page<Medicine> search(String keyword, int page, int size);

    Medicine findById(Long id);

    void save(MedicineRequest request);

    void update(Long id, MedicineRequest request);

    void delete(Long id);
}
