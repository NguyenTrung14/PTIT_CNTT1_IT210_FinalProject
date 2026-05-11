package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.entity.Prescription;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PrescriptionStatus;
import com.example.javaweb_congthongtinbenhvien.repository.MedicineRepository;
import com.example.javaweb_congthongtinbenhvien.repository.PrescriptionDetailRepository;
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
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final UserRepository userRepository;

    @Override
    public List<Prescription> findWaitingDispense() {
        return prescriptionRepository.findFullByStatusOrderByCreatedAtAsc(
                PrescriptionStatus.WAITING_DISPENSE
        );
    }

    @Override
    public List<Prescription> findByDoctorUserId(Long doctorUserId) {
        return prescriptionRepository.findFullByDoctorUserIdOrderByCreatedAtDesc(doctorUserId);
    }

    @Override
    public Prescription findById(Long id) {
        return prescriptionRepository.findFullById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));
    }

    @Override
    public Prescription findByIdForDoctor(Long id, Long doctorUserId) {
        return prescriptionRepository.findFullByIdAndDoctorUserId(id, doctorUserId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay don thuoc"));
    }

    @Override
    @Transactional
    public void dispensePrescription(Long prescriptionId, Long userId) {
        Prescription prescription = findById(prescriptionId);

        if (prescription.getStatus() != PrescriptionStatus.WAITING_DISPENSE) {
            throw new RuntimeException("Chỉ được cấp phát đơn thuốc đang chờ cấp phát");
        }

        if (!prescriptionDetailRepository.existsByPrescriptionId(prescriptionId)) {
            throw new RuntimeException("Don thuoc khong co thuoc");
        }

        User dispensedBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người cấp phát"));

        prescriptionDetailRepository.findFirstInvalidOrInsufficientStockMedicineName(prescriptionId)
                .ifPresent(medicineName -> {
                    throw new RuntimeException("Thuoc " + medicineName + " khong du ton kho hoac so luong khong hop le");
                });

        medicineRepository.deductStockByPrescriptionId(prescriptionId);

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescription.setDispensedAt(LocalDateTime.now());
        prescription.setDispensedBy(dispensedBy);

        prescriptionRepository.save(prescription);
    }
}
