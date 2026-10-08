package com.smarttoll.dto;

import com.smarttoll.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotBlank String transactionId,
        @NotNull PaymentMethod method) {
}