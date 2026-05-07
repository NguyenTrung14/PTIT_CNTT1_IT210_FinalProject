package com.example.javaweb_congthongtinbenhvien.service;

import com.example.javaweb_congthongtinbenhvien.dto.ProfileRequest;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.UserProfile;
import com.example.javaweb_congthongtinbenhvien.entity.enums.UserStatus;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    UserProfile findProfileByUserId(Long userId);

    void updateProfile(ProfileRequest request);

    void updateStatus(Long userId, UserStatus status);
}
