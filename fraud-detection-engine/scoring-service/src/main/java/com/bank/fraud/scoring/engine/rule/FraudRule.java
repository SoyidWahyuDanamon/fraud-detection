package com.bank.fraud.scoring.engine.rule;

import com.bank.fraud.shared.event.TransactionEvent;

public interface FraudRule {
    String name();
    double score(TransactionEvent event);
}