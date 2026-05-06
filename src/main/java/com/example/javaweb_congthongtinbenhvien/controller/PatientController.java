package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.AppointmentRequest;
import com.example.javaweb_congthongtinbenhvien.dto.ProfileRequest;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.UserProfile;
import com.example.javaweb_congthongtinbenhvien.entity.enums.CommonStatus;
import com.example.javaweb_congthongtinbenhvien.repository.DoctorRepository;
import com.example.javaweb_congthongtinbenhvien.repository.SpecialtyRepository;
import com.example.javaweb_congthongtinbenhvien.service.AppointmentService;
import com.example.javaweb_congthongtinbenhvien.service.MedicalRecordService;
import com.example.javaweb_congthongtinbenhvien.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patient")
public class PatientController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("appointments", appointmentService.findByPatientId(loginUser.getId()));

        return "patient/dashboard";
    }

    @GetMapping("/profile")
    public String profileForm(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        User user = userService.findById(loginUser.getId());
        UserProfile profile = userService.findProfileByUserId(user.getId());

        ProfileRequest request = new ProfileRequest();
        request.setUserId(user.getId());
        request.setFullName(user.getFullName());
        request.setPhone(user.getPhone());
        request.setGender(profile.getGender());
        request.setDateOfBirth(profile.getDateOfBirth());
        request.setAddress(profile.getAddress());
        request.setIdentityNumber(profile.getIdentityNumber());
        request.setHealthInsuranceCode(profile.getHealthInsuranceCode());
        request.setEmergencyContact(profile.getEmergencyContact());

        model.addAttribute("profile", request);

        return "patient/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @ModelAttribute("profile") ProfileRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            request.setUserId(loginUser.getId());

            userService.updateProfile(request);

            User updatedUser = userService.findById(loginUser.getId());
            session.setAttribute("loginUser", updatedUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/patient/profile";
    }

    @GetMapping("/appointments/book")
    public String bookForm(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(loginUser.getId());

        model.addAttribute("appointment", request);
        model.addAttribute("specialties", specialtyRepository.findByStatus(CommonStatus.ACTIVE));
        model.addAttribute("doctors", doctorRepository.findByStatus(CommonStatus.ACTIVE));

        return "patient/book-appointment";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(
            @ModelAttribute("appointment") AppointmentRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            request.setPatientId(loginUser.getId());

            appointmentService.bookAppointment(request);

            redirectAttributes.addFlashAttribute("success", "Đặt lịch khám thành công");
            return "redirect:/patient/appointments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/appointments/book";
        }
    }

    @GetMapping("/appointments")
    public String myAppointments(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("appointments", appointmentService.findByPatientId(loginUser.getId()));

        return "patient/my-appointments";
    }

    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Bệnh nhân chủ động hủy lịch") String reason,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");

            appointmentService.cancelAppointment(id, loginUser.getId(), reason);

            redirectAttributes.addFlashAttribute("success", "Hủy lịch thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    @GetMapping("/medical-history")
    public String medicalHistory(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("records", medicalRecordService.findByPatientId(loginUser.getId()));

        return "patient/medical-history";
    }
}