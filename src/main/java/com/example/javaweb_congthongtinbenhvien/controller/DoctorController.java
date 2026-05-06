package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.MedicalRecordRequest;
import com.example.javaweb_congthongtinbenhvien.dto.ProfileRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
import com.example.javaweb_congthongtinbenhvien.entity.Doctor;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.UserProfile;
import com.example.javaweb_congthongtinbenhvien.repository.DoctorRepository;
import com.example.javaweb_congthongtinbenhvien.service.AppointmentService;
import com.example.javaweb_congthongtinbenhvien.service.MedicalRecordService;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import com.example.javaweb_congthongtinbenhvien.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final MedicineService medicineService;
    private final UserService userService;

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

    @GetMapping("/profile")
    public String profileForm(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        User user = userService.findById(loginUser.getId());
        UserProfile userProfile = userService.findProfileByUserId(user.getId());

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bác sĩ"));

        ProfileRequest profile = new ProfileRequest();
        profile.setUserId(user.getId());
        profile.setFullName(user.getFullName());
        profile.setPhone(user.getPhone());
        profile.setGender(userProfile.getGender());
        profile.setAddress(userProfile.getAddress());

        model.addAttribute("profile", profile);
        model.addAttribute("doctor", doctor);

        return "doctor/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @ModelAttribute("profile") ProfileRequest profile,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            profile.setUserId(loginUser.getId());

            userService.updateProfile(profile);

            User updatedUser = userService.findById(loginUser.getId());
            session.setAttribute("loginUser", updatedUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ bác sĩ thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/doctor/profile";
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