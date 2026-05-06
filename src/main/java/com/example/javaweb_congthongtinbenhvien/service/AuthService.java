package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.dto.RegisterRequest;
import com.example.javaweb_congthongtinbenhvien.entity.User;

public interface AuthService {

    User login(String email, String password);

    User register(RegisterRequest request);
}