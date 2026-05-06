package com.example.javaweb_congthongtinbenhvien.config;

import com.example.javaweb_congthongtinbenhvien.entity.*;
import com.example.javaweb_congthongtinbenhvien.entity.enums.*;
import com.example.javaweb_congthongtinbenhvien.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final SpecialtyRepository specialtyRepository;
    private final TestTypeRepository testTypeRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicineRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedSpecialties();
        seedTestTypes();
        seedMedicines();
        seedUsersAndDoctor();
    }

    private void seedSpecialties() {
        createSpecialtyIfNotExists(
                "Nội tổng quát",
                "Khám và điều trị các bệnh nội khoa thông thường"
        );

        createSpecialtyIfNotExists(
                "Tim mạch",
                "Khám và điều trị các bệnh liên quan đến tim mạch"
        );

        createSpecialtyIfNotExists(
                "Da liễu",
                "Khám và điều trị các bệnh về da"
        );

        createSpecialtyIfNotExists(
                "Tai mũi họng",
                "Khám và điều trị các bệnh tai, mũi, họng"
        );

        createSpecialtyIfNotExists(
                "Nhi khoa",
                "Khám và điều trị bệnh cho trẻ em"
        );
    }

    private void createSpecialtyIfNotExists(String name, String description) {
        if (specialtyRepository.existsByName(name)) {
            return;
        }

        Specialty specialty = new Specialty();
        specialty.setName(name);
        specialty.setDescription(description);
        specialty.setStatus(CommonStatus.ACTIVE);

        specialtyRepository.save(specialty);
    }

    private void seedTestTypes() {
        createTestTypeIfNotExists(
                "Xét nghiệm máu",
                "Kiểm tra các chỉ số máu cơ bản"
        );

        createTestTypeIfNotExists(
                "Xét nghiệm nước tiểu",
                "Kiểm tra chỉ số nước tiểu"
        );

        createTestTypeIfNotExists(
                "Chụp X-quang",
                "Chẩn đoán hình ảnh bằng X-quang"
        );

        createTestTypeIfNotExists(
                "Siêu âm",
                "Chẩn đoán hình ảnh bằng siêu âm"
        );
    }

    private void createTestTypeIfNotExists(String name, String description) {
        if (testTypeRepository.existsByName(name)) {
            return;
        }

        TestType testType = new TestType();
        testType.setName(name);
        testType.setDescription(description);
        testType.setStatus(CommonStatus.ACTIVE);

        testTypeRepository.save(testType);
    }

    private void seedMedicines() {
        createMedicineIfNotExists(
                "Paracetamol 500mg",
                "Viên",
                new BigDecimal("1000"),
                500,
                "Thuốc giảm đau, hạ sốt"
        );

        createMedicineIfNotExists(
                "Amoxicillin 500mg",
                "Viên",
                new BigDecimal("2000"),
                300,
                "Thuốc kháng sinh"
        );

        createMedicineIfNotExists(
                "Vitamin C",
                "Viên",
                new BigDecimal("1500"),
                400,
                "Bổ sung vitamin C"
        );

        createMedicineIfNotExists(
                "Natri Clorid 0.9%",
                "Chai",
                new BigDecimal("5000"),
                100,
                "Dung dịch nước muối sinh lý"
        );
    }

    private void createMedicineIfNotExists(
            String name,
            String unit,
            BigDecimal price,
            Integer stockQuantity,
            String description
    ) {
        if (medicineRepository.existsByName(name)) {
            return;
        }

        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setUnit(unit);
        medicine.setPrice(price);
        medicine.setStockQuantity(stockQuantity);
        medicine.setDescription(description);
        medicine.setStatus(MedicineStatus.ACTIVE);

        medicineRepository.save(medicine);
    }

    private void seedUsersAndDoctor() {
        User admin = createUserIfNotExists(
                "admin@gmail.com",
                "123456",
                "Quản trị viên",
                "0900000001",
                Role.ADMIN
        );

        createProfileIfNotExists(
                admin,
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "Hà Nội",
                "001199000001",
                null
        );

        User doctorUser = createUserIfNotExists(
                "doctor@gmail.com",
                "123456",
                "Bác sĩ Nguyễn Văn A",
                "0900000002",
                Role.DOCTOR
        );

        createProfileIfNotExists(
                doctorUser,
                Gender.MALE,
                LocalDate.of(1985, 5, 10),
                "Hà Nội",
                "001198500002",
                null
        );

        User patient = createUserIfNotExists(
                "patient@gmail.com",
                "123456",
                "Bệnh nhân Trần Văn B",
                "0900000003",
                Role.PATIENT
        );

        createProfileIfNotExists(
                patient,
                Gender.MALE,
                LocalDate.of(2003, 8, 15),
                "Hà Nội",
                "001200300003",
                "BH123456"
        );

        createDoctorIfNotExists(doctorUser);
    }

    private User createUserIfNotExists(
            String email,
            String rawPassword,
            String fullName,
            String phone,
            Role role
    ) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(role);
            user.setStatus(UserStatus.ACTIVE);

            return userRepository.save(user);
        });
    }

    private void createProfileIfNotExists(
            User user,
            Gender gender,
            LocalDate dateOfBirth,
            String address,
            String identityNumber,
            String healthInsuranceCode
    ) {
        if (userProfileRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setGender(gender);
        profile.setDateOfBirth(dateOfBirth);
        profile.setAddress(address);
        profile.setIdentityNumber(identityNumber);
        profile.setHealthInsuranceCode(healthInsuranceCode);
        profile.setEmergencyContact("0909999999");

        userProfileRepository.save(profile);
    }

    private void createDoctorIfNotExists(User doctorUser) {
        if (doctorRepository.findByUserId(doctorUser.getId()).isPresent()) {
            return;
        }

        Specialty specialty = specialtyRepository.findByName("Nội tổng quát")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa Nội tổng quát"));

        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);
        doctor.setSpecialty(specialty);
        doctor.setDegree("Thạc sĩ Y khoa");
        doctor.setExperienceYears(8);
        doctor.setRoomNumber("P101");
        doctor.setDescription("Bác sĩ chuyên khoa Nội tổng quát");
        doctor.setStatus(CommonStatus.ACTIVE);

        doctorRepository.save(doctor);
    }
}