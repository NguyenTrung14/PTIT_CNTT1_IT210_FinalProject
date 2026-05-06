package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import com.example.javaweb_congthongtinbenhvien.service.PrescriptionService;
import com.example.javaweb_congthongtinbenhvien.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}