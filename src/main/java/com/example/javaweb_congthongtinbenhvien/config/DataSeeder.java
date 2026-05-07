package com.example.javaweb_congthongtinbenhvien.config;

import com.example.javaweb_congthongtinbenhvien.entity.Doctor;
import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.entity.Specialty;
import com.example.javaweb_congthongtinbenhvien.entity.TestType;
import com.example.javaweb_congthongtinbenhvien.entity.User;
import com.example.javaweb_congthongtinbenhvien.entity.UserProfile;
import com.example.javaweb_congthongtinbenhvien.entity.enums.Gender;
import com.example.javaweb_congthongtinbenhvien.entity.enums.MedicineStatus;
import com.example.javaweb_congthongtinbenhvien.entity.enums.Role;
import com.example.javaweb_congthongtinbenhvien.entity.enums.UserStatus;
import com.example.javaweb_congthongtinbenhvien.repository.DoctorRepository;
import com.example.javaweb_congthongtinbenhvien.repository.MedicineRepository;
import com.example.javaweb_congthongtinbenhvien.repository.SpecialtyRepository;
import com.example.javaweb_congthongtinbenhvien.repository.TestTypeRepository;
import com.example.javaweb_congthongtinbenhvien.repository.UserProfileRepository;
import com.example.javaweb_congthongtinbenhvien.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
                "Hà Nội"
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
                "Hà Nội"
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
                "Hà Nội"
        );

        createDoctorIfNotExists(
                doctorUser,
                0,
                "Bac si Chuyen khoa I",
                8,
                "P201",
                "Bac si phu trach kham noi tong quat"
        );

        User adminDemo = createUserIfNotExists(
                "admin2@gmail.com",
                "123456",
                "Admin Demo",
                "0900000010",
                Role.ADMIN
        );

        createProfileIfNotExists(
                adminDemo,
                Gender.MALE,
                "Ha Noi"
        );

        User doctorUser1 = createUserIfNotExists(
                "doctor1@gmail.com",
                "123456",
                "Bac si Le Minh An",
                "0900000011",
                Role.DOCTOR
        );

        createProfileIfNotExists(
                doctorUser1,
                Gender.MALE,
                "Ha Noi"
        );

        createDoctorIfNotExists(
                doctorUser1,
                0,
                "Bac si Chuyen khoa I",
                6,
                "P202",
                "Bac si kham noi tong quat"
        );

        User doctorUser2 = createUserIfNotExists(
                "doctor2@gmail.com",
                "123456",
                "Bac si Pham Thu Ha",
                "0900000012",
                Role.DOCTOR
        );

        createProfileIfNotExists(
                doctorUser2,
                Gender.FEMALE,
                "Ha Noi"
        );

        createDoctorIfNotExists(
                doctorUser2,
                1,
                "Thac si Bac si",
                9,
                "P203",
                "Bac si chuyen khoa tim mach"
        );

        User doctorUser3 = createUserIfNotExists(
                "doctor3@gmail.com",
                "123456",
                "Bac si Tran Quoc Bao",
                "0900000013",
                Role.DOCTOR
        );

        createProfileIfNotExists(
                doctorUser3,
                Gender.MALE,
                "Ha Noi"
        );

        createDoctorIfNotExists(
                doctorUser3,
                2,
                "Bac si Chuyen khoa II",
                11,
                "P204",
                "Bac si chuyen khoa da lieu"
        );

        User doctorUser4 = createUserIfNotExists(
                "doctor4@gmail.com",
                "123456",
                "Bac si Nguyen Mai Linh",
                "0900000014",
                Role.DOCTOR
        );

        createProfileIfNotExists(
                doctorUser4,
                Gender.FEMALE,
                "Ha Noi"
        );

        createDoctorIfNotExists(
                doctorUser4,
                3,
                "Bac si Chuyen khoa I",
                7,
                "P205",
                "Bac si chuyen khoa tai mui hong"
        );

        User doctorUser5 = createUserIfNotExists(
                "doctor5@gmail.com",
                "123456",
                "Bac si Do Hoang Nam",
                "0900000015",
                Role.DOCTOR
        );

        createProfileIfNotExists(
                doctorUser5,
                Gender.MALE,
                "Ha Noi"
        );

        createDoctorIfNotExists(
                doctorUser5,
                4,
                "Thac si Bac si",
                10,
                "P206",
                "Bac si chuyen khoa nhi"
        );
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
            String address
    ) {
        if (userProfileRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setGender(gender);
        profile.setAddress(address);

        userProfileRepository.save(profile);
    }

    private void createDoctorIfNotExists(
            User doctorUser,
            int specialtyIndex,
            String degree,
            Integer experienceYears,
            String roomNumber,
            String description
    ) {
        if (doctorRepository.findByUserId(doctorUser.getId()).isPresent()) {
            return;
        }

        Specialty specialty = specialtyRepository.findByName("Nội tổng quát")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa Nội tổng quát"));

        var specialties = specialtyRepository.findAll();
        if (!specialties.isEmpty()) {
            specialty = specialties.get(specialtyIndex % specialties.size());
        }

        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);
        doctor.setSpecialty(specialty);
        doctor.setDegree(degree);
        doctor.setExperienceYears(experienceYears);
        doctor.setRoomNumber(roomNumber);
        doctor.setDescription(description);

        doctorRepository.save(doctor);
    }
}
