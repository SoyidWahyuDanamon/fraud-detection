package com.bank.fraud.transaction.repository;

import com.bank.fraud.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String>,
        JpaSpecificationExecutor<Transaction>,
        TransactionRepositoryCustom {

    List<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);

    List<Transaction> findByAccountIdAndTimestampBetweenOrderByTimestamp(
            String accountId, Instant from, Instant to);

    long countByAccountIdAndTimestampAfter(String accountId, Instant after);
}