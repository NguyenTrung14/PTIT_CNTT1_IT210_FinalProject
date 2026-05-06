package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.entity.Prescription;
import com.example.javaweb_congthongtinbenhvien.entity.PrescriptionDetail;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PrescriptionStatus;
import com.example.javaweb_congthongtinbenhvien.repository.MedicineRepository;
import com.example.javaweb_congthongtinbenhvien.repository.PrescriptionRepository;
import com.example.javaweb_congthongtinbenhvien.repository.UserRepository;
import com.example.javaweb_congthongtinbenhvien.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    @Override
    public List<Prescription> findWaitingDispense() {
        return prescriptionRepository.findFullByStatusOrderByCreatedAtAsc(
                PrescriptionStatus.WAITING_DISPENSE
        );
    }

    @Override
    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));
    }

    @Override
    @Transactional
    public void dispensePrescription(Long prescriptionId, Long userId) {
        Prescription prescription = findById(prescriptionId);

        if (prescription.getStatus() != PrescriptionStatus.WAITING_DISPENSE) {
            throw new RuntimeException("Chỉ được cấp phát đơn thuốc đang chờ cấp phát");
        }

        if (prescription.getDetails() == null || prescription.getDetails().isEmpty()) {
            throw new RuntimeException("Đơn thuốc không có thuốc");
        }

        User dispensedBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người cấp phát"));

        for (PrescriptionDetail detail : prescription.getDetails()) {
            Medicine medicine = detail.getMedicine();

            if (medicine == null) {
                throw new RuntimeException("Chi tiết đơn thuốc không hợp lệ");
            }

            if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng thuốc không hợp lệ");
            }

            if (medicine.getStockQuantity() == null || medicine.getStockQuantity() < detail.getQuantity()) {
                throw new RuntimeException("Thuốc " + medicine.getName() + " không đủ tồn kho");
            }
        }

        for (PrescriptionDetail detail : prescription.getDetails()) {
            Medicine medicine = detail.getMedicine();
            medicine.setStockQuantity(medicine.getStockQuantity() - detail.getQuantity());
            medicineRepository.save(medicine);
        }

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescription.setDispensedAt(LocalDateTime.now());
        prescription.setDispensedBy(dispensedBy);

        prescriptionRepository.save(prescription);
    }
}