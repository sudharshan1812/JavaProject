package com.smarttoll.controller;

import com.smarttoll.dto.DashboardResponse;
import com.smarttoll.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    public DashboardResponse statistics() {
        return dashboardService.statistics();
    }

    @GetMapping("/live-transactions")
    public DashboardResponse live() {
        return dashboardService.live();
    }
}