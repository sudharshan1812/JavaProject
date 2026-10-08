package com.smarttoll.dto;

import com.smarttoll.model.enums.PaymentMethod;
import com.smarttoll.model.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PaymentResult(
        String transactionId,
        String paymentId,
        double amount,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime createdAt,
        String receipt) {
}