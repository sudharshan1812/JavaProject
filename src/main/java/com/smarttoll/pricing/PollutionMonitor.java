package com.smarttoll.pricing;

import org.springframework.stereotype.Component;

@Component
public class PollutionMonitor implements PricingStrategy {

    public enum PollutionLevel {
        GOOD("Good", 50, 0.0),
        MODERATE("Moderate", 100, 0.05),
        POOR("Poor", 200, 0.10),
        SEVERE("Severe", 400, 0.20);

        private final String label;
        private final int aqi;
        private final double surchargePercent;

        PollutionLevel(String label, int aqi, double surchargePercent) {
            this.label = label;
            this.aqi = aqi;
            this.surchargePercent = surchargePercent;
        }

        public String getLabel() { return label; }
        public int getAqi() { return aqi; }
        public double getSurchargePercent() { return surchargePercent; }
    }

    private PollutionLevel level = PollutionLevel.GOOD;

    public void setPollutionLevel(PollutionLevel level) { this.level = level; }
    public PollutionLevel getPollutionLevel() { return level; }

    @Override
    public double surchargePercent() {
        return level.getSurchargePercent();
    }
}