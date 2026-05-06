package com.example.javaweb_congthongtinbenhvien.service.impl;

import com.example.javaweb_congthongtinbenhvien.dto.RegisterRequest;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.UserProfile;
import com.example.javaweb_congthongtinbenhvien.entity.enums.Role;
import com.example.javaweb_congthongtinbenhvien.entity.enums.UserStatus;
import com.example.javaweb_congthongtinbenhvien.repository.UserProfileRepository;
import com.example.javaweb_congthongtinbenhvien.repository.UserRepository;
import com.example.javaweb_congthongtinbenhvien.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User login(String email, String password) {
        if (email == null || email.isBlank()) {
            return null;
        }

        if (password == null || password.isBlank()) {
            return null;
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            return null;
        }

        boolean matched = passwordEncoder.matches(password, user.getPassword());

        if (!matched) {
            return null;
        }

        return user;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu xác nhận không được để trống");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (request.getPhone() != null
                && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone().trim())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone() == null ? null : request.getPhone().trim());

        user.setRole(Role.PATIENT);

        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        userProfileRepository.save(profile);

        return savedUser;
    }
}