package com.smarttoll.service;

import com.smarttoll.dto.DashboardResponse;
import com.smarttoll.dto.TransactionDTO;
import com.smarttoll.model.enums.PaymentStatus;
import com.smarttoll.repository.PaymentRepository;
import com.smarttoll.repository.TollTransactionRepository;
import com.smarttoll.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final TollTransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionService transactionService;

    public DashboardService(VehicleRepository vehicleRepository,
                            TollTransactionRepository transactionRepository,
                            PaymentRepository paymentRepository,
                            TransactionService transactionService) {
        this.vehicleRepository = vehicleRepository;
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.transactionService = transactionService;
    }

    public DashboardResponse statistics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long totalVehicles = vehicleRepository.count();
        long todayTransactions = transactionRepository.countByCreatedAtAfter(todayStart);
        double todayRevenue = transactionRepository.revenueSince(todayStart);

        long payments = paymentRepository.count();
        double successRate = payments == 0 ? 100.0
                : (double) paymentRepository.countByStatus(PaymentStatus.SUCCESS) * 100 / payments;

        return DashboardResponse.of(
                totalVehicles,
                todayTransactions,
                todayRevenue,
                successRate,
                transactionService.latest(10));
    }

    public DashboardResponse live() {
        return DashboardResponse.of(
                vehicleRepository.count(),
                transactionRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay()),
                transactionRepository.revenueSince(LocalDate.now().atStartOfDay()),
                100.0,
                transactionService.latest(10));
    }
}