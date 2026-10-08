package com.smarttoll.payment;

import com.smarttoll.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("walletPaymentProcessor")
public class WalletPaymentProcessor implements PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(WalletPaymentProcessor.class);

    @Override
    public void process(Payment payment) {
        log.info("Processing wallet payment {} for amount {}", payment.getPaymentId(), payment.getAmount());
    }
}