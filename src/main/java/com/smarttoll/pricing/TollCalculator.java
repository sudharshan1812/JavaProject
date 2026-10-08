package com.smarttoll.pricing;

import com.smarttoll.model.enums.VehicleType;

import java.time.LocalDateTime;

@FunctionalInterface
public interface TollCalculator {

    double calculate(VehicleType type, double baseToll, LocalDateTime when);
}