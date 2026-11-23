package com.bank.fraud.scoring.engine;

import java.util.List;

public record ScoringResult(
        double riskScore,
        String severity,
        List<String> triggeredRules
) {}