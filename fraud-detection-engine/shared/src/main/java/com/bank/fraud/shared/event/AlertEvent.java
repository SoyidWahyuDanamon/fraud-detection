package com.bank.fraud.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AlertEvent(
        String alertId,
        String transactionId,
        String accountId,
        BigDecimal riskScore,
        List<String> triggeredRules,
        String severity,
        Instant detectedAt
) {}
