package com.bank.fraud.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RunningBalanceDto(
        String transactionId,
        BigDecimal amount,
        Instant timestamp,
        BigDecimal runningBalance
) {}