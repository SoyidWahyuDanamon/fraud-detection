package com.bank.fraud.scoring.service;

import com.bank.fraud.shared.event.AlertEvent;
import com.bank.fraud.shared.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "alerts";

    public void publish(AlertEvent event) {
        Message<AlertEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.KEY, event.accountId())
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published event {} to topic {}",
                                event.transactionId(), TOPIC);
                    } else {
                        log.error("Failed to publish event {}",
                                event.transactionId(), ex);
                    }
                });
    }
}