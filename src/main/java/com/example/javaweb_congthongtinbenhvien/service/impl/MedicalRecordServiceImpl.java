package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.dto.MedicalRecordRequest;
import com.example.javaweb_congthongtinbenhvien.dto.PrescriptionDetailRequest;
import com.example.javaweb_congthongtinbenhvien.entity.*;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PrescriptionStatus;
import com.example.javaweb_congthongtinbenhvien.repository.AppointmentRepository;
import com.example.javaweb_congthongtinbenhvien.repository.MedicalRecordRepository;
import com.example.javaweb_congthongtinbenhvien.repository.MedicineRepository;
import com.example.javaweb_congthongtinbenhvien.repository.PrescriptionRepository;
import com.example.javaweb_congthongtinbenhvien.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecordRequest request) {
        if (request.getAppointmentId() == null) {
            throw new RuntimeException("Lịch khám không được để trống");
        }

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Lịch khám này đã hoàn thành");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Lịch khám đã bị hủy");
        }

        /*
         * Theo SRS CORE-06:
         * Bác sĩ chỉ được khám ca đang ở trạng thái "Chờ khám".
         */
        if (appointment.getStatus() != AppointmentStatus.WAITING) {
            throw new RuntimeException("Chỉ được khám lịch đang ở trạng thái chờ khám");
        }

        if (appointment.getMedicalRecord() != null) {
            throw new RuntimeException("Lịch khám này đã có hồ sơ bệnh án");
        }

        if (request.getSymptoms() == null || request.getSymptoms().isBlank()) {
            throw new RuntimeException("Triệu chứng không được để trống");
        }

        if (request.getDiagnosis() == null || request.getDiagnosis().isBlank()) {
            throw new RuntimeException("Chẩn đoán không được để trống");
        }

        if (request.getPrescriptionDetails() == null || request.getPrescriptionDetails().isEmpty()) {
            throw new RuntimeException("Đơn thuốc phải có ít nhất 1 loại thuốc");
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        medicalRecord.setPatient(appointment.getPatient());
        medicalRecord.setDoctor(appointment.getDoctor());
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        medicalRecord.setNote(request.getNote());

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

        Prescription prescription = new Prescription();
        prescription.setMedicalRecord(savedRecord);
        prescription.setStatus(PrescriptionStatus.WAITING_DISPENSE);

        for (PrescriptionDetailRequest detailRequest : request.getPrescriptionDetails()) {
            if (detailRequest.getMedicineId() == null) {
                continue;
            }

            if (detailRequest.getQuantity() == null || detailRequest.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng thuốc phải lớn hơn 0");
            }

            Medicine medicine = medicineRepository.findById(detailRequest.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));

            PrescriptionDetail detail = new PrescriptionDetail();
            detail.setPrescription(prescription);
            detail.setMedicine(medicine);
            detail.setQuantity(detailRequest.getQuantity());
            detail.setDosage(detailRequest.getDosage());
            detail.setUsageInstruction(detailRequest.getUsageInstruction());

            prescription.getDetails().add(detail);
        }

        if (prescription.getDetails().isEmpty()) {
            throw new RuntimeException("Đơn thuốc phải có ít nhất 1 loại thuốc");
        }

        prescriptionRepository.save(prescription);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return savedRecord;
    }

    @Override
    public MedicalRecord findById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh án"));
    }

    @Override
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return medicalRecordRepository.findFullHistoryByPatientId(patientId);
    }

    @Override
    public List<MedicalRecord> findByDoctorId(Long doctorId) {
        return medicalRecordRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }
}