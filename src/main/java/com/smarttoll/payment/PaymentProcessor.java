package com.smarttoll.payment;

import com.smarttoll.model.Payment;

public interface PaymentProcessor {

    void process(Payment payment);
}