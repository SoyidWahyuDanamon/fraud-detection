package com.bank.fraud.audit.consumer;

import com.bank.fraud.audit.entity.AlertDocument;
import com.bank.fraud.audit.repository.AlertDocumentRepository;
import com.bank.fraud.shared.event.AlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertEventConsumer {

    private final AlertDocumentRepository alertRepository;

    @KafkaListener(topics = "alerts", groupId = "audit-group")
    public void onAlert(AlertEvent event, Acknowledgment ack) {
        try {
            AlertDocument doc = new AlertDocument();
            doc.setAlertId(event.alertId());
            doc.setTransactionId(event.transactionId());
            doc.setAccountId(event.accountId());
            doc.setRiskScore(event.riskScore().doubleValue());
            doc.setTriggeredRules(event.triggeredRules());
            doc.setSeverity(event.severity());
            doc.setDetectedAt(event.detectedAt());

            alertRepository.save(doc);
            log.info("Alert {} indexed to Elasticsearch", event.alertId());
        } finally {
            ack.acknowledge();
        }
    }
}