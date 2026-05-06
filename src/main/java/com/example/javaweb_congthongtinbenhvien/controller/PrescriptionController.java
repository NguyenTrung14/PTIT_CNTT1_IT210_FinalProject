package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.service.PrescriptionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping("/waiting")
    public String waiting(Model model) {
        model.addAttribute("prescriptions", prescriptionService.findWaitingDispense());
        return "admin/prescriptions/waiting";
    }

    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("prescription", prescriptionService.findById(id));
        return "admin/prescriptions/detail";
    }

    @GetMapping("/dispense/{id}")
    public String dispense(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");

            prescriptionService.dispensePrescription(id, loginUser.getId());

            redirectAttributes.addFlashAttribute("success", "Cấp phát thuốc thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/prescriptions/waiting";
    }
}