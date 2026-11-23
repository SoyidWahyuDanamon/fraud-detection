package com.bank.fraud.shared.event;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionEvent(
        String transactionId,
        String accountId,
        String merchantId,
        String merchantName,
        String category,
        BigDecimal amount,
        String currency,
        String channel,
        String countryCode,
        Instant timestamp
) {}
