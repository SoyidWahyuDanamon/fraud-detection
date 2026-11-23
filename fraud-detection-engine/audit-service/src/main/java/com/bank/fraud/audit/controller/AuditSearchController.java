package com.bank.fraud.audit.controller;

import com.bank.fraud.audit.entity.AlertDocument;
import com.bank.fraud.audit.repository.AlertDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditSearchController {

    private final AlertDocumentRepository alertRepository;

    @GetMapping("/alerts/{accountId}")
    public ResponseEntity<Page<AlertDocument>> getByAccount(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AlertDocument> result = alertRepository.findByAccountId(
                accountId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "detectedAt")));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/alerts/{accountId}/high-risk")
    public ResponseEntity<List<AlertDocument>> getHighRisk(
            @PathVariable String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since,
            @RequestParam(defaultValue = "0.5") double minScore) {
        List<AlertDocument> results = alertRepository.searchByAccountAndScore(accountId, since, minScore);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/alerts/critical")
    public ResponseEntity<List<AlertDocument>> getCriticalAlerts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        List<AlertDocument> results = alertRepository.findBySeverityAndDetectedAtAfter("CRITICAL", since);
        return ResponseEntity.ok(results);
    }
}