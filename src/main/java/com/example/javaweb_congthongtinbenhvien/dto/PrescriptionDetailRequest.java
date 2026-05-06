package com.example.javaweb_congthongtinbenhvien.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrescriptionDetailRequest {

    private Long medicineId;

    private Integer quantity;

    private String dosage;

    private String usageInstruction;
}