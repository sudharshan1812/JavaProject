package com.smarttoll.pricing;

import org.springframework.stereotype.Component;

@Component
public class TrafficMonitor implements PricingStrategy {

    public enum TrafficLevel {
        LOW("Low", 0.10),
        MEDIUM("Medium", 0.25),
        HIGH("High", 0.50);

        private final String label;
        private final double surchargePercent;

        TrafficLevel(String label, double surchargePercent) {
            this.label = label;
            this.surchargePercent = surchargePercent;
        }

        public String getLabel() { return label; }
        public double getSurchargePercent() { return surchargePercent; }
    }

    private TrafficLevel level = TrafficLevel.MEDIUM;

    public void setTrafficLevel(TrafficLevel level) { this.level = level; }
    public TrafficLevel getTrafficLevel() { return level; }

    @Override
    public double surchargePercent() {
        return level.getSurchargePercent();
    }
}