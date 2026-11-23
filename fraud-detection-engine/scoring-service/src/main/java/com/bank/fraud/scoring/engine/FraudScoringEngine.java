package com.bank.fraud.scoring.engine;

import com.bank.fraud.scoring.engine.rule.FraudRule;
import com.bank.fraud.shared.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FraudScoringEngine {

    private final List<FraudRule> rules;

    public FraudScoringEngine(List<FraudRule> rules) {
        this.rules = rules;
        log.info("FraudScoringEngine initialized with {} rules: {}", rules.size(),
                rules.stream().map(FraudRule::name).toList());
    }

    public ScoringResult score(TransactionEvent event) {
        List<Map.Entry<String, Double>> ruleScores = rules.stream()
                .map(rule -> {
                    try {
                        double s = rule.score(event);
                        return Map.entry(rule.name(), s);
                    } catch (Exception e) {
                        log.error("Rule {} failed for transaction {}", rule.name(), event.transactionId(), e);
                        return Map.entry(rule.name(), 0.0);
                    }
                })
                .filter(e -> e.getValue() > 0)
                .toList();

        if (ruleScores.isEmpty()) {
            return new ScoringResult(0.0, "NONE", List.of());
        }

        double total = ruleScores.stream().mapToDouble(Map.Entry::getValue).sum();
        double normalized = Math.min(1.0, total / rules.size());
        String severity = determineSeverity(normalized);
        List<String> triggered = ruleScores.stream().map(Map.Entry::getKey).toList();

        log.info("Scoring complete for txn {}: score={}, severity={}, rules={}",
                event.transactionId(), String.format("%.3f", normalized), severity, triggered);

        return new ScoringResult(normalized, severity, triggered);
    }

    private String determineSeverity(double score) {
        return switch ((Double) score) {
            case Double d when d >= 0.8 -> "CRITICAL";
            case Double d when d >= 0.6 -> "HIGH";
            case Double d when d >= 0.3 -> "MEDIUM";
            default -> "LOW";
        };
    }
}