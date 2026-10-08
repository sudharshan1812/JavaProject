package com.smarttoll.dto;

import jakarta.validation.constraints.NotBlank;

public record RFIDScanRequest(@NotBlank String tagId) {
}