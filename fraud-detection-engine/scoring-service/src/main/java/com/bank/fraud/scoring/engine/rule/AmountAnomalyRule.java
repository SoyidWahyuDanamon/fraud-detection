package com.bank.fraud.scoring.engine.rule;

import com.bank.fraud.shared.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AmountAnomalyRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String name() {
        return "AMOUNT_ANOMALY";
    }

    @Override
    public double score(TransactionEvent event) {
        String key = "baseline:avg:" + event.accountId() + ":" + event.category();
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached == null) return 0.0;

        double avg = ((Number) cached).doubleValue();
        if (avg == 0) return 0.0;

        double ratio = event.amount().doubleValue() / avg;
        if (ratio > 3.0) {
            double score = Math.min(1.0, (ratio - 1.0) / 5.0);
            log.debug("AmountAnomalyRule triggered for {}: ratio={}", event.accountId(), String.format("%.2f", ratio));
            return score;
        }
        return 0.0;
    }
}