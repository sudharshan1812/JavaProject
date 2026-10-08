package com.smarttoll.dto;

import com.smarttoll.model.enums.TransactionStatus;
import com.smarttoll.pricing.TollBreakdown;

public record ScanResult(
        String transactionId,
        String tagId,
        String registrationNumber,
        String vehicleType,
        String ownerName,
        TollBreakdown breakdown,
        TransactionStatus status) {
}