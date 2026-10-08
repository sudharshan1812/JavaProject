package com.smarttoll.dto;

import com.smarttoll.model.enums.VehicleType;
import jakarta.validation.constraints.NotNull;

public record TollCalculationRequest(@NotNull VehicleType vehicleType) {
}