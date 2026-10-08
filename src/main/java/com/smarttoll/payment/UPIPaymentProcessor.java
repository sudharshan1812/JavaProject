package com.smarttoll.payment;

import com.smarttoll.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("upiPaymentProcessor")
public class UPIPaymentProcessor implements PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(UPIPaymentProcessor.class);

    @Override
    public void process(Payment payment) {
        log.info("Processing UPI payment {} for amount {}", payment.getPaymentId(), payment.getAmount());
    }
}