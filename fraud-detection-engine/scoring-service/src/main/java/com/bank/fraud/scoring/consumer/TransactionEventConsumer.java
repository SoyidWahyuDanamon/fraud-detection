package com.bank.fraud.scoring.consumer;

import com.bank.fraud.scoring.engine.FraudScoringEngine;
import com.bank.fraud.scoring.engine.ScoringResult;
import com.bank.fraud.scoring.service.AlertPublisher;
import com.bank.fraud.shared.event.AlertEvent;
import com.bank.fraud.shared.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final FraudScoringEngine engine;
    private final AlertPublisher alertPublisher;

    @KafkaListener(topics = "transactions", groupId = "scoring-group")
    public void onTransaction(TransactionEvent event, Acknowledgment ack) {
        try {
            ScoringResult result = engine.score(event);

            if (result.riskScore() > 0.1) {
                AlertEvent alert = new AlertEvent(
                        UUID.randomUUID().toString(),
                        event.transactionId(),
                        event.accountId(),
                        java.math.BigDecimal.valueOf(result.riskScore()),
                        result.triggeredRules(),
                        result.severity(),
                        Instant.now()
                );
                alertPublisher.publish(alert);
                log.info("Alert generated: txn={}, severity={}, score={}",
                        event.transactionId(), result.severity(), String.format("%.3f", result.riskScore()));
            }
        } finally {
            ack.acknowledge();
        }
    }
}