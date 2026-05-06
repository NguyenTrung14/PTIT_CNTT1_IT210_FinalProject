package com.example.javaweb_congthongtinbenhvien.service;

import java.math.BigDecimal;
import java.util.List;

public interface ReportService {

    BigDecimal totalPaidRevenue();

    List<Object[]> revenueByMonth();

    List<Object[]> topDoctors();
}
