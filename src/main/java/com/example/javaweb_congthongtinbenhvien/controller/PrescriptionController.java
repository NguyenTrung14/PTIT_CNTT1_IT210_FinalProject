package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.service.PrescriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping({
            "/admin/prescriptions/waiting",
            "/doctor/prescriptions/waiting"
    })
    public String waiting(
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("prescriptions", prescriptionService.findWaitingDispense());
        model.addAttribute("basePath", getBasePath(request));

        return "admin/prescriptions/waiting";
    }

    @GetMapping({
            "/admin/prescriptions/detail/{id}",
            "/doctor/prescriptions/detail/{id}"
    })
    public String detail(
            @PathVariable Long id,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("prescription", prescriptionService.findById(id));
        model.addAttribute("basePath", getBasePath(request));

        return "admin/prescriptions/detail";
    }

    @GetMapping({
            "/admin/prescriptions/dispense/{id}",
            "/doctor/prescriptions/dispense/{id}"
    })
    public String dispense(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        String basePath = getBasePath(request);

        try {
            User loginUser = (User) session.getAttribute("loginUser");

            if (loginUser == null) {
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập");
                return "redirect:/auth/login";
            }

            prescriptionService.dispensePrescription(id, loginUser.getId());
            redirectAttributes.addFlashAttribute("success", "Cấp phát thuốc thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:" + basePath + "/prescriptions/waiting";
    }

    private String getBasePath(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/doctor")) {
            return "/doctor";
        }

        return "/admin";
    }
}