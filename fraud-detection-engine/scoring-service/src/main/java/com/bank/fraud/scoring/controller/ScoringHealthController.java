package com.bank.fraud.scoring.controller;

import com.bank.fraud.scoring.engine.FraudScoringEngine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/scoring")
public class ScoringHealthController {

    private final FraudScoringEngine engine;

    public ScoringHealthController(FraudScoringEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/rules")
    public Map<String, Object> getRules() {
        return Map.of(
                "activeRules", "3",
                "description", "Velocity, AmountAnomaly, MerchantPattern"
        );
    }
}