package com.smarttoll.pricing;

import com.smarttoll.model.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DynamicPricing {

    private final TrafficMonitor traffic;
    private final WeatherService weather;
    private final PollutionMonitor pollution;
    private final PeakHourService peakHour;

    public DynamicPricing(TrafficMonitor traffic, WeatherService weather,
                          PollutionMonitor pollution, PeakHourService peakHour) {
        this.traffic = traffic;
        this.weather = weather;
        this.pollution = pollution;
        this.peakHour = peakHour;
    }

    public double baseRate(VehicleType type) {
        return switch (type) {
            case CAR -> 80.0;
            case TRUCK -> 180.0;
            case BUS -> 120.0;
            case EMERGENCY -> 0.0;
        };
    }

    public TollBreakdown breakdown(VehicleType type) {
        LocalDateTime now = LocalDateTime.now();
        double base = baseRate(type);

        TollBreakdown.Conditions conditions = new TollBreakdown.Conditions(
                traffic.getTrafficLevel().getLabel(),
                weather.getWeather().getLabel(),
                pollution.getPollutionLevel().getAqi(),
                peakHour.isPeak(now),
                now);

        if (type == VehicleType.EMERGENCY) {
            return new TollBreakdown(type, conditions,
                    new TollBreakdown.Charges(0, 0, 0, 0, 0, 0));
        }

        TollCalculator calculator = (t, b, when) -> Math.round(
                b + b * traffic.surchargePercent()
                  + b * weather.surchargePercent()
                  + b * pollution.surchargePercent()
                  + peakHour.peakCharge(when));

        double total = calculator.calculate(type, base, now);
        TrafficMonitor.TrafficLevel trafficLevel = traffic.getTrafficLevel();
        WeatherService.Weather weatherNow = weather.getWeather();

        double trafficCharge = Math.round(base * trafficLevel.getSurchargePercent());
        double weatherCharge = Math.round(base * weatherNow.getSurchargePercent());
        double pollutionCharge = Math.round(base * pollution.getPollutionLevel().getSurchargePercent());
        double peakCharge = peakHour.peakCharge(now);

        return new TollBreakdown(type, conditions,
                new TollBreakdown.Charges(base, trafficCharge, peakCharge, weatherCharge, pollutionCharge, total));
    }
}