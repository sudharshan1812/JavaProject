package com.smarttoll.dto;

import com.smarttoll.model.enums.TransactionStatus;

import java.time.LocalDateTime;

public record TransactionDTO(
        String transactionId,
        String registrationNumber,
        String vehicleType,
        String ownerName,
        String rfidTagId,
        double baseToll,
        double trafficCharge,
        double peakCharge,
        double weatherCharge,
        double pollutionCharge,
        double finalAmount,
        TransactionStatus status,
        LocalDateTime createdAt) {

    public static TransactionDTO of(com.smarttoll.model.TollTransaction tx) {
        return new TransactionDTO(
                tx.getTransactionId(),
                tx.getVehicle() != null ? tx.getVehicle().getRegistrationNumber() : "-",
                tx.getVehicle() != null ? tx.getVehicle().type().name() : "-",
                tx.getVehicle() != null ? tx.getVehicle().getOwnerName() : "-",
                tx.getRfidTagId(),
                tx.getBaseToll(), tx.getTrafficCharge(), tx.getPeakCharge(),
                tx.getWeatherCharge(), tx.getPollutionCharge(), tx.getFinalAmount(),
                tx.getStatus(), tx.getCreatedAt());
    }
}