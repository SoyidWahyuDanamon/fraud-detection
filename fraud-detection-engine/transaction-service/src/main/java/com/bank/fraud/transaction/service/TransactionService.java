package com.bank.fraud.transaction.service;

import com.bank.fraud.shared.dto.TransactionDto;
import com.bank.fraud.shared.dto.TransactionResponseDto;
import com.bank.fraud.shared.event.TransactionEvent;
import com.bank.fraud.transaction.entity.Transaction;
import com.bank.fraud.transaction.dto.AnomalyDto;
import com.bank.fraud.transaction.dto.CategorySpendDto;
import com.bank.fraud.transaction.dto.RunningBalanceDto;
import com.bank.fraud.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public TransactionResponseDto createTransaction(TransactionDto dto) {
        // Update baseline in Redis for scoring-service to use
        updateBaseline(dto);

        Transaction transaction = Transaction.builder()
                .accountId(dto.accountId())
                .merchantId(dto.merchantId())
                .merchantName(dto.merchantName())
                .category(dto.category())
                .amount(dto.amount())
                .currency(dto.currency())
                .channel(dto.channel())
                .countryCode(dto.countryCode())
                .timestamp(Instant.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Publish event to Kafka
        TransactionEvent event = new TransactionEvent(
                saved.getTransactionId(),
                saved.getAccountId(),
                saved.getMerchantId(),
                saved.getMerchantName(),
                saved.getCategory(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getChannel(),
                saved.getCountryCode(),
                saved.getTimestamp()
        );
        eventPublisher.publish(event);

        log.info("Transaction {} created and published to Kafka", saved.getTransactionId());

        return new TransactionResponseDto(
                saved.getTransactionId(),
                saved.getAccountId(),
                saved.getMerchantName(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getChannel(),
                saved.getTimestamp()
        );
    }

    private void updateBaseline(TransactionDto dto) {
        String key = "baseline:avg:" + dto.accountId() + ":" + dto.category();
        redisTemplate.opsForValue().set(key, dto.amount().doubleValue(), Duration.ofHours(24));
    }

    public List<TransactionResponseDto> getByAccount(String accountId) {
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId).stream()
                .map(t -> new TransactionResponseDto(
                        t.getTransactionId(),
                        t.getAccountId(),
                        t.getMerchantName(),
                        t.getAmount(),
                        t.getCurrency(),
                        t.getChannel(),
                        t.getTimestamp()
                ))
                .toList();
    }

    public List<RunningBalanceDto> getRunningBalance(String accountId, LocalDate from, LocalDate to) {
        return transactionRepository.getRunningBalance(accountId, from, to);
    }

    public List<CategorySpendDto> getAverageSpendByCategory(String accountId, int days) {
        return transactionRepository.getAverageSpendByCategory(accountId, days);
    }

    public List<AnomalyDto> detectAnomalies(String accountId) {
        return transactionRepository.detectAnomalies(accountId);
    }
}