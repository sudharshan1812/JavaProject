package com.smarttoll.payment;

import com.smarttoll.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("cardPaymentProcessor")
public class CardPaymentProcessor implements PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(CardPaymentProcessor.class);

    @Override
    public void process(Payment payment) {
        log.info("Processing card payment {} for amount {}", payment.getPaymentId(), payment.getAmount());
    }
}