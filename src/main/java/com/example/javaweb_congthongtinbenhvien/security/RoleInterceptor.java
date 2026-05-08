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
            response.sendRedirect(request.getContextPath() + "/error/403?loginRequired=true");
            return false;
        }

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/error/403?loginRequired=true");
            return false;
        }

        String uri = request.getServletPath();
        Role role = loginUser.getRole();

        if (uri.startsWith("/admin") && role != Role.ADMIN) {
            saveAccessDeniedReturnUrl(request, session, role);
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if (uri.startsWith("/doctor") && role != Role.DOCTOR) {
            saveAccessDeniedReturnUrl(request, session, role);
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        if (uri.startsWith("/patient") && role != Role.PATIENT) {
            saveAccessDeniedReturnUrl(request, session, role);
            response.sendRedirect(request.getContextPath() + "/error/403");
            return false;
        }

        return true;
    }

    private void saveAccessDeniedReturnUrl(HttpServletRequest request, HttpSession session, Role role) {
        String referer = request.getHeader("Referer");
        String contextPath = request.getContextPath();
        String baseUrl = request.getScheme() + "://" + request.getServerName();

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }

        if (referer != null && referer.startsWith(baseUrl) && !referer.contains("/error/403")) {
            session.setAttribute("accessDeniedReturnUrl", referer);
            return;
        }

        session.setAttribute("accessDeniedReturnUrl", contextPath + dashboardPath(role));
    }

    private String dashboardPath(Role role) {
        if (role == Role.ADMIN) {
            return "/admin/dashboard";
        }

        if (role == Role.DOCTOR) {
            return "/doctor/dashboard";
        }

        return "/patient/dashboard";
    }
}
