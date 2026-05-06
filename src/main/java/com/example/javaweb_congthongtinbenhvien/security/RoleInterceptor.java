package com.example.javaweb_congthongtinbenhvien.security;

import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        String uri = request.getRequestURI();
        Role role = loginUser.getRole();

        if (uri.startsWith("/admin") && role != Role.ADMIN) {
            response.sendRedirect("/error/403");
            return false;
        }

        if (uri.startsWith("/doctor") && role != Role.DOCTOR) {
            response.sendRedirect("/error/403");
            return false;
        }

        if (uri.startsWith("/patient") && role != Role.PATIENT) {
            response.sendRedirect("/error/403");
            return false;
        }

        return true;
    }
}