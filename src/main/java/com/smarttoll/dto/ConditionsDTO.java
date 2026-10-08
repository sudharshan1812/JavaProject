package com.smarttoll.dto;

import java.time.LocalDateTime;

public record ConditionsDTO(
        String traffic,
        String trafficLabel,
        String weather,
        String weatherLabel,
        int aqi,
        String pollution,
        String pollutionLabel,
        boolean peak,
        LocalDateTime updatedAt) {

    public ConditionsDTO(String traffic, String trafficLabel, String weather, String weatherLabel,
                         int aqi, String pollution, String pollutionLabel, boolean peak) {
        this(traffic, trafficLabel, weather, weatherLabel, aqi, pollution, pollutionLabel, peak, LocalDateTime.now());
    }
}