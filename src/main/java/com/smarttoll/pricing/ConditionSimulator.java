package com.smarttoll.pricing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

// ponytail: one shared scheduler thread, fixed 20s cycle; add an ExecutorService
// per-lane if multiple concurrent simulators are ever needed
@Component
public class ConditionSimulator {

    private static final Logger log = LoggerFactory.getLogger(ConditionSimulator.class);

    private final TrafficMonitor traffic;
    private final WeatherService weather;
    private final PollutionMonitor pollution;

    public ConditionSimulator(TrafficMonitor traffic, WeatherService weather, PollutionMonitor pollution) {
        this.traffic = traffic;
        this.weather = weather;
        this.pollution = pollution;
    }

    @Scheduled(fixedDelay = 20000)
    public void fluctuate() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        TrafficMonitor.TrafficLevel[] trafficLevels = TrafficMonitor.TrafficLevel.values();
        WeatherService.Weather[] weathers = WeatherService.Weather.values();
        PollutionMonitor.PollutionLevel[] pollutionLevels = PollutionMonitor.PollutionLevel.values();
        traffic.setTrafficLevel(trafficLevels[rng.nextInt(trafficLevels.length)]);
        weather.setWeather(weathers[rng.nextInt(weathers.length)]);
        pollution.setPollutionLevel(pollutionLevels[rng.nextInt(pollutionLevels.length)]);
        log.info("[{}] conditions -> traffic={}, weather={}, pollution={}",
                Thread.currentThread().getName(),
                traffic.getTrafficLevel().getLabel(),
                weather.getWeather().getLabel(),
                pollution.getPollutionLevel().getLabel());
    }
}
