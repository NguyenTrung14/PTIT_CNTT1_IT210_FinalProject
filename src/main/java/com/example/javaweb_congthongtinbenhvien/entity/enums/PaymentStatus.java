package com.example.javaweb_congthongtinbenhvien.entity.enums;

public enum PaymentStatus {
    PENDING,        // Chờ thanh toán
    PAID,           // Đã thanh toán
    FAILED,         // Thanh toán thất bại
    CANCELLED,      // Đã hủy
    REFUNDED        // Đã hoàn tiền
}