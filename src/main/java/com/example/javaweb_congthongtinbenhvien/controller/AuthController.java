package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.RegisterRequest;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.enums.Role;
import com.example.javaweb_congthongtinbenhvien.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^(0|\\+84)[0-9]{9}$";

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            HttpSession session,
            Model model
    ) {
        try {
            Map<String, String> fieldErrors = validateLogin(email, password);

            if (!fieldErrors.isEmpty()) {
                model.addAttribute("fieldErrors", fieldErrors);
                model.addAttribute("email", email);
                return "auth/login";
            }

            User user = authService.login(email, password);

            if (user == null) {
                model.addAttribute("error", "Email hoac mat khau khong dung");
                model.addAttribute("email", email);
                return "auth/login";
            }

            session.setAttribute("loginUser", user);

            if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin/dashboard";
            }

            if (user.getRole() == Role.DOCTOR) {
                return "redirect:/doctor/dashboard";
            }

            return "redirect:/patient/dashboard";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("registerRequest") RegisterRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Map<String, String> fieldErrors = validateRegister(request);

            if (!fieldErrors.isEmpty()) {
                model.addAttribute("fieldErrors", fieldErrors);
                model.addAttribute("registerRequest", request);
                return "auth/register";
            }

            authService.register(request);
            redirectAttributes.addFlashAttribute("success", "Dang ky thanh cong, vui long dang nhap");
            return "redirect:/auth/login";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerRequest", request);
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    private Map<String, String> validateLogin(String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || email.isBlank()) {
            errors.put("email", "Email khong duoc de trong");
        } else if (!email.trim().matches(EMAIL_REGEX)) {
            errors.put("email", "Email khong dung dinh dang");
        }

        if (password == null || password.isBlank()) {
            errors.put("password", "Mat khau khong duoc de trong");
        }

        return errors;
    }

    private Map<String, String> validateRegister(RegisterRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.getFullName() == null || request.getFullName().isBlank()) {
            errors.put("fullName", "Ho ten khong duoc de trong");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            errors.put("email", "Email khong duoc de trong");
        } else if (!request.getEmail().trim().matches(EMAIL_REGEX)) {
            errors.put("email", "Email khong dung dinh dang");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            errors.put("password", "Mat khau khong duoc de trong");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            errors.put("confirmPassword", "Xac nhan mat khau khong duoc de trong");
        } else if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            errors.put("confirmPassword", "Mat khau xac nhan khong khop");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            errors.put("phone", "So dien thoai khong duoc de trong");
        } else if (!request.getPhone().trim().matches(PHONE_REGEX)) {
            errors.put("phone", "So dien thoai khong dung dinh dang");
        }

        return errors;
    }
}
