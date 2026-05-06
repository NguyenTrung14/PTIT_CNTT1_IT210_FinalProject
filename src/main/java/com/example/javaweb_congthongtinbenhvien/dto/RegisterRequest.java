package com.example.javaweb_congthongtinbenhvien.dto;

import com.example.javaweb_congthongtinbenhvien.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;

    private String password;

    private String confirmPassword;

    private String fullName;

    private String phone;

    private Role role;
}