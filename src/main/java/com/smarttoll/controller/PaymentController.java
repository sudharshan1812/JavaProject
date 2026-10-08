package com.smarttoll.controller;

import com.smarttoll.dto.PaymentRequest;
import com.smarttoll.dto.PaymentResult;
import com.smarttoll.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResult pay(@Valid @RequestBody PaymentRequest request) {
        return paymentService.pay(request);
    }
}