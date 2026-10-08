package com.smarttoll.controller;

import com.smarttoll.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/revenue")
    public Map<String, Double> revenue() {
        return reportService.revenue();
    }

    @GetMapping("/vehicle-distribution")
    public Map<String, Long> vehicleDistribution() {
        return reportService.vehicleDistribution();
    }

    @GetMapping("/payment-methods")
    public Map<String, Double> paymentMethods() {
        return reportService.paymentMethodSplit();
    }

    @GetMapping("/daily-revenue")
    public Map<String, Double> dailyRevenue(@RequestParam(defaultValue = "7") int days) {
        return reportService.dailyRevenue(days);
    }
}