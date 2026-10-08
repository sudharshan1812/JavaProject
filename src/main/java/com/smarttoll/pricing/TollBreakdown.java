package com.smarttoll.pricing;

import com.smarttoll.model.enums.VehicleType;

import java.time.LocalDateTime;

public record TollBreakdown(
        VehicleType vehicleType,
        Conditions conditions,
        Charges charges) {

    public record Conditions(String traffic, String weather, int aqi, boolean peak, LocalDateTime time) {
    }

    public record Charges(double baseToll, double trafficCharge, double peakCharge,
                          double weatherCharge, double pollutionCharge, double finalAmount) {
    }
}