package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicineStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "medicines",
        indexes = {
                @Index(name = "idx_medicines_name", columnList = "name"),
                @Index(name = "idx_medicines_status", columnList = "status")
        }
)
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORE-04: Admin CRUD danh mục thuốc
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    // Ví dụ: viên, chai, gói, hộp
    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    // CORE-08: quản lý tồn kho thuốc
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicineStatus status = MedicineStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (price == null) {
            price = BigDecimal.ZERO;
        }

        if (stockQuantity == null) {
            stockQuantity = 0;
        }

        if (status == null) {
            status = MedicineStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}