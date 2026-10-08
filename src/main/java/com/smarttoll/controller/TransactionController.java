package com.smarttoll.controller;

import com.smarttoll.dto.TransactionDTO;
import com.smarttoll.model.TollTransaction;
import com.smarttoll.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionDTO> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        if (from != null && to != null) {
            return transactionService.between(from, to);
        }
        return transactionService.latest(10);
    }

    @GetMapping("/pending")
    public List<TransactionDTO> pending() {
        return transactionService.pending();
    }

    @GetMapping("/{id}")
    public TollTransaction get(@PathVariable String id) {
        return transactionService.getByTransactionId(id);
    }
}