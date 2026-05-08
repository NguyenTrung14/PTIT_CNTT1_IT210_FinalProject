package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.AppointmentRequest;
import com.example.javaweb_congthongtinbenhvien.dto.ProfileRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Appointment;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        UserProfile userProfile = userService.findProfileByUserId(user.getId());

        ProfileRequest profile = new ProfileRequest();
        profile.setUserId(user.getId());
        profile.setFullName(user.getFullName());
        profile.setPhone(user.getPhone());
        profile.setGender(userProfile.getGender());
        profile.setAddress(userProfile.getAddress());

        model.addAttribute("profile", profile);

        return "patient/profile";
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

        populateBookAppointmentForm(model, request);

        return "patient/book-appointment";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(
            @ModelAttribute("appointment") AppointmentRequest request,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            request.setPatientId(loginUser.getId());

            Appointment appointment = appointmentService.bookAppointment(request);

            redirectAttributes.addFlashAttribute("success", "Dat lich thanh cong. Vui long thanh toan de xac nhan lich kham.");
            return "redirect:/patient/payments/checkout/" + appointment.getId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            populateBookAppointmentForm(model, request);
            return "patient/book-appointment";
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

    private void populateBookAppointmentForm(Model model, AppointmentRequest request) {
        model.addAttribute("appointment", request);
        model.addAttribute("specialties", specialtyRepository.findByStatus(CommonStatus.ACTIVE));
        model.addAttribute("doctors", doctorRepository.findByStatus(CommonStatus.ACTIVE));
    }
}
