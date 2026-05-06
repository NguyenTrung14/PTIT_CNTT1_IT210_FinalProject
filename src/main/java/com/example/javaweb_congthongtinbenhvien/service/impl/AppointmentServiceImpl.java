package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.dto.AppointmentRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.Doctor;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import com.example.javaweb_congthongtinbenhvien.repository.AppointmentRepository;
import com.example.javaweb_congthongtinbenhvien.repository.DoctorRepository;
import com.example.javaweb_congthongtinbenhvien.repository.UserRepository;
import com.example.javaweb_congthongtinbenhvien.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public Appointment bookAppointment(AppointmentRequest request) {
        validateAppointmentTime(request);

        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        boolean exists = appointmentRepository.existsOverlapAppointment(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                List.of(
                        AppointmentStatus.PENDING,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.WAITING
                )
        );

        if (exists) {
            throw new RuntimeException("Khung giờ này đã có người đặt");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setReason(request.getReason());

        /*
         * Theo SRS:
         * Bệnh nhân đặt lịch xong thì lịch ở trạng thái chờ khám.
         * Bác sĩ sẽ nhìn thấy lịch này ở màn hình /doctor/appointments.
         */
        appointment.setStatus(AppointmentStatus.WAITING);

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescStartTimeDesc(patientId);
    }

    @Override
    public List<Appointment> findByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAscStartTimeAsc(doctorId);
    }

    @Override
    public List<Appointment> findWaitingByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                doctorId,
                AppointmentStatus.WAITING
        );
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, Long patientId, String cancelReason) {
        Appointment appointment = findById(appointmentId);

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch này");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Lịch đã khám xong, không thể hủy");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Lịch đã bị hủy trước đó");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getStartTime()
        );

        if (LocalDateTime.now().isAfter(appointmentDateTime.minusHours(24))) {
            throw new RuntimeException("Chỉ được hủy lịch trước giờ khám ít nhất 24 giờ");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(cancelReason);

        appointmentRepository.save(appointment);
    }

    private void validateAppointmentTime(AppointmentRequest request) {
        if (request.getPatientId() == null) {
            throw new RuntimeException("Bệnh nhân không được để trống");
        }

        if (request.getDoctorId() == null) {
            throw new RuntimeException("Bác sĩ không được để trống");
        }

        if (request.getAppointmentDate() == null) {
            throw new RuntimeException("Ngày khám không được để trống");
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("Giờ khám không được để trống");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("Giờ bắt đầu phải nhỏ hơn giờ kết thúc");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                request.getAppointmentDate(),
                request.getStartTime()
        );

        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không được đặt lịch trong quá khứ");
        }
    }
}