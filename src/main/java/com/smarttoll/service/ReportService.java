package com.smarttoll.service;

import com.smarttoll.model.Vehicle;
import com.smarttoll.model.enums.PaymentMethod;
import com.smarttoll.repository.PaymentRepository;
import com.smarttoll.repository.TollTransactionRepository;
import com.smarttoll.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final VehicleRepository vehicleRepository;
    private final TollTransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;

    public ReportService(VehicleRepository vehicleRepository,
                         TollTransactionRepository transactionRepository,
                         PaymentRepository paymentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
    }

    public Map<String, Double> revenue() {
        Map<String, Double> revenue = new LinkedHashMap<>();
        revenue.put("today", transactionRepository.revenueSince(LocalDate.now().atStartOfDay()));
        revenue.put("week", transactionRepository.revenueSince(LocalDate.now().minusDays(7).atStartOfDay()));
        revenue.put("month", transactionRepository.revenueSince(LocalDate.now().minusDays(30).atStartOfDay()));
        revenue.put("total", transactionRepository.revenueSince(LocalDateTime.parse("2000-01-01T00:00:00")));
        return revenue;
    }

    public Map<String, Long> vehicleDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        vehicleRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(v -> v.type().name(), LinkedHashMap::new,
                        java.util.stream.Collectors.counting()))
                .forEach(distribution::put);
        return distribution;
    }

    public Map<String, Double> paymentMethodSplit() {
        Map<String, Double> split = new LinkedHashMap<>();
        paymentRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(p -> p.getPaymentMethod().name(),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.summingDouble(com.smarttoll.model.Payment::getAmount)))
                .forEach(split::put);
        return split;
    }

    public Map<String, Double> dailyRevenue(int days) {
        Map<String, Double> daily = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            daily.put(day.toString(),
                    transactionRepository.revenueSince(day.atStartOfDay())
                            - transactionRepository.revenueSince(day.plusDays(1).atStartOfDay()));
        }
        return daily;
    }

    public long totalTransactions() {
        return transactionRepository.count();
    }
}