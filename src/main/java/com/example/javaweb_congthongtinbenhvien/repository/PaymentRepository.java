package com.example.javaweb_congthongtinbenhvien.repository;

import com.example.javaweb_congthongtinbenhvien.entity.Payment;
import com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    List<Payment> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByTransactionCode(String transactionCode);

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus.PAID
            """)
    BigDecimal totalPaidRevenue();

    @Query("""
            select year(p.paidAt), month(p.paidAt), coalesce(sum(p.amount), 0)
            from Payment p
            where p.status = com.example.javaweb_congthongtinbenhvien.entity.enums.PaymentStatus.PAID
              and p.paidAt is not null
            group by year(p.paidAt), month(p.paidAt)
            order by year(p.paidAt) desc, month(p.paidAt) desc
            """)
    List<Object[]> revenueByMonth();
}
