package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicineStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineStatus status = MedicineStatus.ACTIVE;

    @OneToMany(mappedBy = "medicine")
    private List<PrescriptionDetail> prescriptionDetails = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (stockQuantity == null) {
            stockQuantity = 0;
        }

        if (price == null) {
            price = BigDecimal.ZERO;
        }

        if (status == null) {
            status = MedicineStatus.ACTIVE;
        }
    }
}