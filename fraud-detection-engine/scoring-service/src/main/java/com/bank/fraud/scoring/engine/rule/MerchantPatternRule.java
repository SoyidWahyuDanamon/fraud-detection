package com.bank.fraud.scoring.engine.rule;

import com.bank.fraud.shared.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MerchantPatternRule implements FraudRule {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int UNIQUE_MERCHANT_THRESHOLD = 4;
    private static final int WINDOW_HOURS = 1;

    @Override
    public String name() {
        return "MERCHANT_PATTERN";
    }

    @Override
    @SuppressWarnings("unchecked")
    public double score(TransactionEvent event) {
        String key = "merchant:set:" + event.accountId();
        long now = event.timestamp().toEpochMilli();
        long windowStart = now - WINDOW_HOURS * 60L * 60L * 1000L;

        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
        redisTemplate.opsForZSet().add(key, event.merchantId(), (double) now);
        redisTemplate.expire(key, Duration.ofHours(WINDOW_HOURS + 1));

        Long uniqueMerchants = redisTemplate.opsForZSet().zCard(key);
        if (uniqueMerchants != null && uniqueMerchants >= UNIQUE_MERCHANT_THRESHOLD) {
            double score = Math.min(1.0, (double) uniqueMerchants / (UNIQUE_MERCHANT_THRESHOLD * 2.0));
            log.debug("MerchantPatternRule triggered for {}: {} unique merchants", event.accountId(), uniqueMerchants);
            return score;
        }
        return 0.0;
    }
}