package com.smarttoll.pricing;

@FunctionalInterface
public interface PricingStrategy {

    double surchargePercent();
}