package com.example.javaweb_congthongtinbenhvien.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {

    @GetMapping("/error/403")
    public String forbidden(
            @RequestParam(defaultValue = "false") boolean loginRequired,
            Model model
    ) {
        if (loginRequired) {
            model.addAttribute("title", "Bạn cần đăng nhập");
            model.addAttribute("message", "Bạn đang truy cập trực tiếp vào trang yêu cầu đăng nhập. Vui lòng đăng nhập để tiếp tục.");
            model.addAttribute("showLogout", false);
        } else {
            model.addAttribute("title", "Bạn không có quyền truy cập");
            model.addAttribute("message", "Tài khoản hiện tại không được phép truy cập trang này. Vui lòng quay lại đúng trang chức năng của bạn.");
            model.addAttribute("showLogout", true);
        }

        return "error/403";
    }

    @GetMapping("/error/404")
    public String notFound() {
        return "error/404";
    }
}
