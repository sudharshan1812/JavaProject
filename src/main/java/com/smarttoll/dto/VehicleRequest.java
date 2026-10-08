package com.smarttoll.dto;

import com.smarttoll.model.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequest(
        @NotNull VehicleType vehicleType,
        @NotBlank String registrationNumber,
        @NotBlank String ownerName,
        Integer numberOfSeats,
        Double loadCapacity,
        Integer passengerCapacity,
        String emergencyService,
        String rfidTagId) {
}