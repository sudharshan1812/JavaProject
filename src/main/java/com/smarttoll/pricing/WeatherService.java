package com.smarttoll.pricing;

import org.springframework.stereotype.Component;

@Component
public class WeatherService implements PricingStrategy {

    public enum Weather {
        SUNNY("Sunny", 0.0),
        RAINY("Rain", 0.10),
        FOG("Fog", 0.15),
        STORM("Storm", 0.25);

        private final String label;
        private final double surchargePercent;

        Weather(String label, double surchargePercent) {
            this.label = label;
            this.surchargePercent = surchargePercent;
        }

        public String getLabel() { return label; }
        public double getSurchargePercent() { return surchargePercent; }
    }

    private Weather weather = Weather.SUNNY;

    public void setWeather(Weather weather) { this.weather = weather; }
    public Weather getWeather() { return weather; }

    @Override
    public double surchargePercent() {
        return weather.getSurchargePercent();
    }
}