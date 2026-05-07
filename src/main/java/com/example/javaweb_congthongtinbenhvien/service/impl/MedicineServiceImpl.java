package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.dto.MedicineRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicineStatus;
import com.example.javaweb_congthongtinbenhvien.repository.MedicineRepository;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    public List<Medicine> findAllActive() {
        return medicineRepository.findByStatus(MedicineStatus.ACTIVE);
    }

    @Override
    public List<Medicine> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAllActive();
        }

        return medicineRepository.findByNameContainingIgnoreCaseAndStatus(
                keyword.trim(),
                MedicineStatus.ACTIVE
        );
    }

    @Override
    public Medicine findById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));
    }

    @Override
    @Transactional
    public void save(MedicineRequest request) {
        validateMedicine(request);

        Medicine medicine = medicineRepository.findByName(request.getName())
                .map(existing -> {
                    if (existing.getStatus() == MedicineStatus.ACTIVE) {
                        throw new RuntimeException("Tên thuốc đã tồn tại");
                    }
                    return existing;
                })
                .orElseGet(() -> medicineRepository
                        .findFirstByStatusOrderByIdAsc(MedicineStatus.DELETED)
                        .orElseGet(Medicine::new));
        medicine.setName(request.getName());
        medicine.setUnit(request.getUnit());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setDescription(request.getDescription());
        medicine.setStatus(request.getStatus() == null ? MedicineStatus.ACTIVE : request.getStatus());

        medicineRepository.save(medicine);
    }

    @Override
    @Transactional
    public void update(Long id, MedicineRequest request) {
        validateMedicine(request);

        Medicine medicine = findById(id);

        medicineRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Tên thuốc đã tồn tại");
            }
        });

        medicine.setName(request.getName());
        medicine.setUnit(request.getUnit());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setDescription(request.getDescription());
        medicine.setStatus(request.getStatus() == null ? MedicineStatus.ACTIVE : request.getStatus());

        medicineRepository.save(medicine);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Medicine medicine = findById(id);
        medicine.setStatus(MedicineStatus.DELETED);
        medicineRepository.save(medicine);
    }

    private void validateMedicine(MedicineRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Tên thuốc không được để trống");
        }

        if (request.getUnit() == null || request.getUnit().isBlank()) {
            throw new RuntimeException("Đơn vị thuốc không được để trống");
        }

        if (request.getPrice() == null) {
            request.setPrice(BigDecimal.ZERO);
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá thuốc phải >= 0");
        }

        if (request.getStockQuantity() == null) {
            request.setStockQuantity(0);
        }

        if (request.getStockQuantity() < 0) {
            throw new RuntimeException("Số lượng tồn kho phải >= 0");
        }
    }
}
