package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.dto.MedicineRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Medicine;

import java.util.List;

public interface MedicineService {

    List<Medicine> findAllActive();

    List<Medicine> search(String keyword);

    Medicine findById(Long id);

    void save(MedicineRequest request);

    void update(Long id, MedicineRequest request);

    void delete(Long id);
}