package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.CommonStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "test_types")
public class TestType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ví dụ: Xét nghiệm máu, X-quang, Siêu âm
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommonStatus status = CommonStatus.ACTIVE;

    @OneToMany(mappedBy = "testType")
    private List<MedicalTest> medicalTests = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = CommonStatus.ACTIVE;
        }
    }
}