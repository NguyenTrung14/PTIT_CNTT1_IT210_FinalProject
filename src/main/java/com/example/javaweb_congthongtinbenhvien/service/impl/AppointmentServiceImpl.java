package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.dto.AppointmentRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.Doctor;
import com.example.javaweb_congthongtinbenhvien.entity.Payment;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.enums.AppointmentStatus;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentMethod;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus;
import com.example.javaweb_congthongtinbenhvien.repository.AppointmentRepository;
import com.example.javaweb_congthongtinbenhvien.repository.DoctorRepository;
import com.example.javaweb_congthongtinbenhvien.repository.PaymentRepository;
import com.example.javaweb_congthongtinbenhvien.repository.UserRepository;
import com.example.javaweb_congthongtinbenhvien.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PaymentRepository paymentRepository;

    private static final BigDecimal APPOINTMENT_FEE = new BigDecimal("100000");
    private static final long MIN_PATIENT_APPOINTMENT_GAP_MINUTES = 30;

    @Override
    @Transactional
    public Appointment bookAppointment(AppointmentRequest request) {
        if (request.getStartTime() != null) {
            request.setEndTime(request.getStartTime().plusMinutes(MIN_PATIENT_APPOINTMENT_GAP_MINUTES));
        }
        validateAppointmentTime(request);
        LocalTime endTime = request.getStartTime().plusMinutes(MIN_PATIENT_APPOINTMENT_GAP_MINUTES);
        request.setEndTime(endTime);

        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        if (request.getSpecialtyId() == null) {
            throw new RuntimeException("Chuyen khoa khong duoc de trong");
        }

        if (!doctor.getSpecialty().getId().equals(request.getSpecialtyId())) {
            throw new RuntimeException("Bac si khong thuoc chuyen khoa da chon");
        }

        List<AppointmentStatus> activeStatuses = List.of(
                AppointmentStatus.PENDING,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.WAITING
        );

        if (hasPatientScheduleConflict(request, activeStatuses.stream().map(Enum::name).toList())) {
            throw new RuntimeException("Lich kham cua ban phai cach lich khac toi thieu 30 phut");
        }

        boolean exists = appointmentRepository.existsOverlapAppointment(
                request.getDoctorId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                endTime,
                activeStatuses
        );

        if (exists) {
            throw new RuntimeException("Khung giờ này đã có người đặt");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setReason(request.getReason());

        
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        Payment payment = new Payment();
        payment.setPatient(patient);
        payment.setAppointment(savedAppointment);
        payment.setAmount(APPOINTMENT_FEE);
        payment.setMethod(PaymentMethod.VNPAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDescription("Phi dat lich kham #" + savedAppointment.getId());
        paymentRepository.save(payment);

        return savedAppointment;
    }

    @Override
    @Transactional
    public List<Appointment> findByPatientId(Long patientId) {
        rejectOverdueAppointments();
        return appointmentRepository.findByPatientIdOrderByAppointmentTimeAsc(patientId);
    }

    @Override
    @Transactional
    public List<Appointment> findByDoctorId(Long doctorId) {
        rejectOverdueAppointments();
        return appointmentRepository.findByDoctorIdOrderByAppointmentTimeAsc(doctorId);
    }

    @Override
    @Transactional
    public List<Appointment> findWaitingByDoctorId(Long doctorId) {
        rejectOverdueAppointments();
        return appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentTimeAsc(
                doctorId,
                AppointmentStatus.WAITING.name()
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

        paymentRepository.findByAppointmentId(appointmentId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.CANCELLED);
                payment.setFailureReason("Benh nhan huy lich");
                paymentRepository.save(payment);
            }
        });
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

        if (request.getStartTime() == null) {
            throw new RuntimeException("Giờ khám không được để trống");
        }

        if (false) {
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

    private boolean hasPatientScheduleConflict(AppointmentRequest request, List<String> activeStatuses) {
        return appointmentRepository.countPatientScheduleConflicts(
                request.getPatientId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                MIN_PATIENT_APPOINTMENT_GAP_MINUTES,
                activeStatuses
        ) > 0;
    }

    private void rejectOverdueAppointments() {
        List<AppointmentStatus> expirableStatuses = List.of(
                AppointmentStatus.PENDING,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.WAITING
        );

        List<Appointment> overdueAppointments = appointmentRepository.findOverdueAppointments(
                LocalDate.now(),
                LocalTime.now(),
                expirableStatuses
        );

        for (Appointment appointment : overdueAppointments) {
            appointment.setStatus(AppointmentStatus.REJECTED);
            appointment.setCancelReason("Lich kham qua thoi gian nhung benh nhan chua den kham");

            Payment payment = appointment.getPayment();
            if (payment != null && payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.CANCELLED);
                payment.setFailureReason("Lich kham qua thoi gian");
            } else if (payment != null && payment.getStatus() == PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setRefundedAt(LocalDateTime.now());
                payment.setFailureReason("Lich kham qua thoi gian nhung benh nhan chua den kham");
            }
        }
    }
}
