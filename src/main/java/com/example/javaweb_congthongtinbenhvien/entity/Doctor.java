package com.example.javaweb_congthongtinbenhvien.entity;

import com.example.javaweb_congthongtinbenhvien.entity.enums.CommonStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "doctors",
        indexes = {
                @Index(name = "idx_doctors_user_id", columnList = "user_id"),
                @Index(name = "idx_doctors_specialty_id", columnList = "specialty_id")
        }
)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User có role DOCTOR
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Bác sĩ thuộc 1 chuyên khoa
    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(length = 100)
    private String degree;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears = 0;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommonStatus status = CommonStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (experienceYears == null) {
            experienceYears = 0;
        }

        if (status == null) {
            status = CommonStatus.ACTIVE;
        }
    }
}