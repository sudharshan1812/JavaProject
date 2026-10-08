package com.smarttoll.pricing;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PeakHourService {

    private static final double PEAK_CHARGE = 15.0;
    private static final int PEAK_START_HOUR = 17;
    private static final int PEAK_END_HOUR = 21;

    public boolean isPeak(LocalDateTime time) {
        int hour = time.getHour();
        return (hour >= 8 && hour < 11) || (hour >= PEAK_START_HOUR && hour < PEAK_END_HOUR);
    }

    public double peakCharge(LocalDateTime time) {
        return isPeak(time) ? PEAK_CHARGE : 0.0;
    }
}