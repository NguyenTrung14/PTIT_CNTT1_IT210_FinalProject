package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.ProfileRequest;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.UserProfile;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import com.example.javaweb_congthongtinbenhvien.service.PrescriptionService;
import com.example.javaweb_congthongtinbenhvien.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final MedicineService medicineService;
    private final PrescriptionService prescriptionService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("medicines", medicineService.findAllActive());
        model.addAttribute("waitingPrescriptions", prescriptionService.findWaitingDispense());

        return "admin/dashboard";
    }

    @GetMapping("/admin/profile")
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

        return "admin/profile";
    }

    @PostMapping("/admin/profile")
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

            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ Admin thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/profile";
    }
}