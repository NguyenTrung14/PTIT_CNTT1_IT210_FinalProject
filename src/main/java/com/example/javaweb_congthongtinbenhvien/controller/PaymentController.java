package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.entity.Payment;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patient/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/checkout/{appointmentId}")
    public String checkout(
            @PathVariable Long appointmentId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = getLoginUser(session);
            Payment payment = paymentService.findByAppointmentId(appointmentId, loginUser.getId());
            model.addAttribute("payment", payment);
            return "patient/payment-checkout";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/appointments";
        }
    }

    @PostMapping("/confirm/{appointmentId}")
    public String confirm(
            @PathVariable Long appointmentId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User loginUser = getLoginUser(session);
            paymentService.confirmPayment(appointmentId, loginUser.getId());
            redirectAttributes.addFlashAttribute("success", "Thanh toan thanh cong. Lich kham da duoc xac nhan.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    private User getLoginUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new RuntimeException("Ban can dang nhap");
        }

        return loginUser;
    }
}
