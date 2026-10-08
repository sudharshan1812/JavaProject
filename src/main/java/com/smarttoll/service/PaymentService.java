package com.smarttoll.service;

import com.smarttoll.dto.PaymentRequest;
import com.smarttoll.dto.PaymentResult;
import com.smarttoll.exception.PaymentException;
import com.smarttoll.model.Payment;
import com.smarttoll.model.TollTransaction;
import com.smarttoll.model.enums.PaymentMethod;
import com.smarttoll.model.enums.PaymentStatus;
import com.smarttoll.model.enums.TransactionStatus;
import com.smarttoll.payment.PaymentProcessor;
import com.smarttoll.repository.PaymentRepository;
import com.smarttoll.repository.TollTransactionRepository;
import com.smarttoll.util.ReceiptGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TollTransactionRepository transactionRepository;
    private final ReceiptGenerator receiptGenerator;
    private final Map<String, PaymentProcessor> processors;

    public PaymentService(PaymentRepository paymentRepository,
                          TollTransactionRepository transactionRepository,
                          ReceiptGenerator receiptGenerator,
                          Map<String, PaymentProcessor> processors) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.receiptGenerator = receiptGenerator;
        this.processors = processors;
    }

    @Transactional
    public PaymentResult pay(PaymentRequest request) {
        TollTransaction transaction = transactionRepository.findByTransactionId(request.transactionId())
                .orElseThrow(() -> new PaymentException("Transaction not found: " + request.transactionId()));

        if (transaction.getStatus() == TransactionStatus.PAID) {
            throw new PaymentException("Transaction " + request.transactionId() + " is already paid");
        }

        Payment payment = new Payment();
        payment.setTransaction(transaction);
        payment.setPaymentMethod(request.method());
        payment.setPaymentId("PAY-" + ThreadLocalRandom.current().nextInt(100000, 999999));
        payment.setAmount(transaction.getFinalAmount());
        payment.setStatus(PaymentStatus.SUCCESS);

        PaymentProcessor processor = processors.get(processorName(request.method()));
        if (processor == null) {
            throw new PaymentException("Unsupported payment method: " + request.method());
        }
        processor.process(payment);

        paymentRepository.save(payment);
        transaction.setStatus(TransactionStatus.PAID);
        transactionRepository.save(transaction);

        String receipt = null;
        try {
            receipt = receiptGenerator.generate(transaction, payment);
        } catch (IOException e) {
            throw new PaymentException("Failed to generate receipt: " + e.getMessage());
        }

        return new PaymentResult(
                transaction.getTransactionId(),
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt(),
                receipt);
    }

    private String processorName(PaymentMethod method) {
        return switch (method) {
            case UPI -> "upiPaymentProcessor";
            case CARD -> "cardPaymentProcessor";
            case WALLET -> "walletPaymentProcessor";
        };
    }
}