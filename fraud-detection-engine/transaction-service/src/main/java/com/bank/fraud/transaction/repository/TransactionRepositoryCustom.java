package com.bank.fraud.transaction.repository;

import com.bank.fraud.transaction.dto.AnomalyDto;
import com.bank.fraud.transaction.dto.CategorySpendDto;
import com.bank.fraud.transaction.dto.RunningBalanceDto;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepositoryCustom {

    List<RunningBalanceDto> getRunningBalance(String accountId, LocalDate from, LocalDate to);

    List<CategorySpendDto> getAverageSpendByCategory(String accountId, int days);

    List<AnomalyDto> detectAnomalies(String accountId);
}