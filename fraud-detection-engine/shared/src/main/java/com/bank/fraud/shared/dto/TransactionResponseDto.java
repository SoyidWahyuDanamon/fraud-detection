package com.bank.fraud.shared.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDto(
        String transactionId,
        String accountId,
        String merchantName,
        BigDecimal amount,
        String currency,
        String channel,
        Instant timestamp
) {}