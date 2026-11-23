package com.bank.fraud.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AnomalyDto(
        String transactionId,
        BigDecimal amount,
        Instant timestamp,
        BigDecimal avg30d,
        double zScore
) {}