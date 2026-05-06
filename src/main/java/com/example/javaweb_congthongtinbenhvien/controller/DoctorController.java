package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.MedicalRecordRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.Doctor;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.repository.DoctorRepository;
import com.example.javaweb_congthongtinbenhvien.service.AppointmentService;
import com.example.javaweb_congthongtinbenhvien.service.MedicalRecordService;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final MedicineService medicineService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        Doctor doctor = doctorRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bác sĩ"));

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointmentService.findByDoctorId(doctor.getId()));

        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        Doctor doctor = doctorRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bác sĩ"));

        model.addAttribute("appointments", appointmentService.findByDoctorId(doctor.getId()));

        return "doctor/appointments";
    }

    @GetMapping("/examine/{appointmentId}")
    public String examineForm(
            @PathVariable Long appointmentId,
            Model model,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");

        Doctor doctor = doctorRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bác sĩ"));

        Appointment appointment = appointmentService.findById(appointmentId);

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/error/403";
        }

        MedicalRecordRequest request = new MedicalRecordRequest();
        request.setAppointmentId(appointmentId);

        model.addAttribute("appointment", appointment);
        model.addAttribute("medicalRecord", request);
        model.addAttribute("medicines", medicineService.findAllActive());

        return "doctor/examine";
    }

    @PostMapping("/examine")
    public String examine(
            @ModelAttribute("medicalRecord") MedicalRecordRequest request,
            RedirectAttributes redirectAttributes
    ) {
        try {
            medicalRecordService.createMedicalRecord(request);
            redirectAttributes.addFlashAttribute("success", "Lưu kết quả khám thành công");
            return "redirect:/doctor/appointments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/doctor/examine/" + request.getAppointmentId();
        }
    }
}