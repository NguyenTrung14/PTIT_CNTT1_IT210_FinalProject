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
        return prescriptionRepository.findByStatusOrderByCreatedAtAsc(
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

        if (prescription.getStatus() == PrescriptionStatus.DISPENSED) {
            throw new RuntimeException("Đơn thuốc đã được cấp phát");
        }

        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new RuntimeException("Đơn thuốc đã bị hủy");
        }

        if (prescription.getDetails() == null || prescription.getDetails().isEmpty()) {
            throw new RuntimeException("Đơn thuốc không có thuốc");
        }

        for (PrescriptionDetail detail : prescription.getDetails()) {
            Medicine medicine = detail.getMedicine();

            if (medicine.getStockQuantity() < detail.getQuantity()) {
                throw new RuntimeException(
                        "Thuốc " + medicine.getName() + " không đủ tồn kho"
                );
            }
        }

        for (PrescriptionDetail detail : prescription.getDetails()) {
            Medicine medicine = detail.getMedicine();
            medicine.setStockQuantity(medicine.getStockQuantity() - detail.getQuantity());
            medicineRepository.save(medicine);
        }

        User dispensedBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người cấp phát"));

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescription.setDispensedAt(LocalDateTime.now());
        prescription.setDispensedBy(dispensedBy);

        prescriptionRepository.save(prescription);
    }
}