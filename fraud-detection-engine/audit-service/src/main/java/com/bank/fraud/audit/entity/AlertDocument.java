package com.bank.fraud.audit.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(indexName = "fraud-alerts")
@Setting(settingPath = "es-settings.json")
@Mapping(mappingPath = "es-mapping.json")
public class AlertDocument {

    @Id
    private String alertId;

    @Field(type = FieldType.Keyword)
    private String transactionId;

    @Field(type = FieldType.Keyword)
    private String accountId;

    @Field(type = FieldType.Double)
    private double riskScore;

    @Field(type = FieldType.Keyword)
    private List<String> triggeredRules;

    @Field(type = FieldType.Keyword)
    private String severity;

    @Field(type = FieldType.Date)
    private Instant detectedAt;

    // Getters & Setters
    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public List<String> getTriggeredRules() { return triggeredRules; }
    public void setTriggeredRules(List<String> triggeredRules) { this.triggeredRules = triggeredRules; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }
}