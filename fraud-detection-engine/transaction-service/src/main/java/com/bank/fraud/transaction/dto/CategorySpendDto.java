package com.bank.fraud.transaction.dto;

import java.math.BigDecimal;

public record CategorySpendDto(
        String category,
        BigDecimal avgDailySpend,
        BigDecimal maxDailySpend,
        int activeDays
) {}