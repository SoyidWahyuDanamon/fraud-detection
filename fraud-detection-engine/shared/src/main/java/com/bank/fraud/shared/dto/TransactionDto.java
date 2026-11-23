package com.bank.fraud.shared.dto;

import java.math.BigDecimal;

public record TransactionDto(
        String accountId,
        String merchantId,
        String merchantName,
        String category,
        BigDecimal amount,
        String currency,
        String channel,
        String countryCode
) {}