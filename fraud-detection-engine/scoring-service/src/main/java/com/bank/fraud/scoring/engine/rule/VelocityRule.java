package com.bank.fraud.scoring.engine.rule;

import com.bank.fraud.shared.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class VelocityRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int THRESHOLD = 5;
    private static final int WINDOW_MINUTES = 10;

    @Override
    public String name() {
        return "VELOCITY";
    }

    @Override
    @SuppressWarnings("unchecked")
    public double score(TransactionEvent event) {
        String key = "velocity:" + event.accountId();
        long now = event.timestamp().toEpochMilli();
        long windowStart = now - WINDOW_MINUTES * 60L * 1000L;

        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
        redisTemplate.opsForZSet().add(key, event.transactionId(), (double) now);
        redisTemplate.expire(key, Duration.ofMinutes(WINDOW_MINUTES + 5L));

        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count != null && count >= THRESHOLD) {
            double score = Math.min(1.0, (double) count / (THRESHOLD * 2.0));
            log.debug("VelocityRule triggered for {}: {} txns in {} min", event.accountId(), count, WINDOW_MINUTES);
            return score;
        }
        return 0.0;
    }
}