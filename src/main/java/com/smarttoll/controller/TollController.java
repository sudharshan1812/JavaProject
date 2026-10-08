package com.smarttoll.controller;

import com.smarttoll.dto.ConditionsDTO;
import com.smarttoll.dto.TollCalculationRequest;
import com.smarttoll.pricing.TollBreakdown;
import com.smarttoll.service.TollPricingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/toll")
public class TollController {

    private final TollPricingService pricingService;

    public TollController(TollPricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/calculate")
    public TollBreakdown calculate(@Valid @RequestBody TollCalculationRequest request) {
        return pricingService.calculate(request.vehicleType());
    }

    @GetMapping("/current-rates")
    public ConditionsDTO currentRates() {
        return pricingService.currentConditions();
    }

    @PutMapping("/conditions")
    public ConditionsDTO updateConditions(@RequestBody ConditionsDTO conditions) {
        return pricingService.updateConditions(conditions);
    }
}