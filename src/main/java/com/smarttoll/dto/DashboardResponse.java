package com.smarttoll.dto;

import com.smarttoll.model.User;

public record DashboardResponse(
        long totalVehicles,
        long todayTransactions,
        double todayRevenue,
        double paymentSuccessRate,
        java.util.List<TransactionDTO> liveTransactions) {

    public static DashboardResponse of(long vehicles, long tx, double revenue, double rate,
                                       java.util.List<TransactionDTO> live) {
        return new DashboardResponse(vehicles, tx, revenue, rate, live);
    }
}