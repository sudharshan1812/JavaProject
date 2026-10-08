package com.smarttoll.repository;

import com.smarttoll.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    long countByStatus(com.smarttoll.model.enums.PaymentStatus status);
}