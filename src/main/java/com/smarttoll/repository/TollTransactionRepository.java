package com.smarttoll.repository;

import com.smarttoll.model.TollTransaction;
import com.smarttoll.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TollTransactionRepository extends JpaRepository<TollTransaction, Long> {

    Optional<TollTransaction> findByTransactionId(String transactionId);

    boolean existsByVehicleId(Long vehicleId);

    List<TollTransaction> findTop10ByOrderByIdDesc();

    List<TollTransaction> findByStatusOrderByIdDesc(TransactionStatus status);

    long countByCreatedAtAfter(LocalDateTime time);

    @Query("select coalesce(sum(t.finalAmount), 0) from TollTransaction t where t.createdAt >= :from and t.status <> 'FAILED'")
    double revenueSince(LocalDateTime from);

    List<TollTransaction> findByCreatedAtBetweenOrderByIdDesc(LocalDateTime from, LocalDateTime to);
}