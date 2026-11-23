package com.bank.fraud.transaction.controller;

import com.bank.fraud.shared.dto.TransactionDto;
import com.bank.fraud.shared.dto.TransactionResponseDto;
import com.bank.fraud.transaction.dto.AnomalyDto;
import com.bank.fraud.transaction.dto.CategorySpendDto;
import com.bank.fraud.transaction.dto.RunningBalanceDto;
import com.bank.fraud.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@Valid @RequestBody TransactionDto dto) {
        TransactionResponseDto response = transactionService.createTransaction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<TransactionResponseDto>> getByAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.getByAccount(accountId));
    }

    @GetMapping("/{accountId}/running-balance")
    public ResponseEntity<List<RunningBalanceDto>> getRunningBalance(
            @PathVariable String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(transactionService.getRunningBalance(accountId, from, to));
    }

    @GetMapping("/{accountId}/category-spend")
    public ResponseEntity<List<CategorySpendDto>> getCategorySpend(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(transactionService.getAverageSpendByCategory(accountId, days));
    }

    @GetMapping("/{accountId}/anomalies")
    public ResponseEntity<List<AnomalyDto>> getAnomalies(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.detectAnomalies(accountId));
    }
}