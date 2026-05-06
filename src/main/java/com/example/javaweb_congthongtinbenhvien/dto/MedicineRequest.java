package com.example.javaweb_congthongtinbenhvien.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MedicineRequest {

    private Long id;

    private String name;

    private String unit;

    private BigDecimal price;

    private Integer stockQuantity;

    private String description;
}