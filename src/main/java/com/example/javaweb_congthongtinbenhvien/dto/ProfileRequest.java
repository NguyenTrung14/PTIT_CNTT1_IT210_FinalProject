package com.example.javaweb_congthongtinbenhvien.dto;

import com.example.javaweb_congthongtinbenhvien.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileRequest {

    private Long userId;

    private String fullName;

    private String phone;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String address;

    private String identityNumber;

    private String healthInsuranceCode;

    private String emergencyContact;
}