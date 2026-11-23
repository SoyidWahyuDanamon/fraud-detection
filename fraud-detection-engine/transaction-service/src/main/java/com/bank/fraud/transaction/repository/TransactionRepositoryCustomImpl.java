package com.bank.fraud.transaction.repository;

import com.bank.fraud.transaction.dto.AnomalyDto;
import com.bank.fraud.transaction.dto.CategorySpendDto;
import com.bank.fraud.transaction.dto.RunningBalanceDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RunningBalanceDto> getRunningBalance(String accountId, LocalDate from, LocalDate to) {
        String sql = """
            SELECT
                t.transaction_id,
                t.amount,
                t.timestamp,
                SUM(t.amount) OVER (
                    PARTITION BY t.account_id
                    ORDER BY t.timestamp
                    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                ) AS running_balance
            FROM transactions t
            WHERE t.account_id = ?
              AND t.timestamp BETWEEN ? AND ?
            ORDER BY t.timestamp
        """;
        return jdbcTemplate.query(sql,
                new Object[]{accountId, from.atStartOfDay(), to.atTime(LocalTime.MAX)},
                (rs, rowNum) -> new RunningBalanceDto(
                        rs.getString("transaction_id"),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getBigDecimal("running_balance")
                ));
    }

    @Override
    public List<CategorySpendDto> getAverageSpendByCategory(String accountId, int days) {
        String sql = """
            WITH daily_spend AS (
                SELECT
                    category,
                    CAST(timestamp AS DATE) AS spend_date,
                    SUM(amount) AS daily_total
                FROM transactions
                WHERE account_id = ?
                  AND timestamp >= NOW() - INTERVAL '1 day' * ?
                GROUP BY category, CAST(timestamp AS DATE)
            )
            SELECT
                category,
                AVG(daily_total) AS avg_daily_spend,
                MAX(daily_total) AS max_daily_spend,
                COUNT(*) AS active_days
            FROM daily_spend
            GROUP BY category
            HAVING AVG(daily_total) > 0
            ORDER BY avg_daily_spend DESC
        """;
        return jdbcTemplate.query(sql,
                new Object[]{accountId, days},
                (rs, rowNum) -> new CategorySpendDto(
                        rs.getString("category"),
                        rs.getBigDecimal("avg_daily_spend"),
                        rs.getBigDecimal("max_daily_spend"),
                        rs.getInt("active_days")
                ));
    }

    @Override
    public List<AnomalyDto> detectAnomalies(String accountId) {
        String sql = """
            WITH baseline AS (
                SELECT
                    transaction_id,
                    amount,
                    timestamp,
                    AVG(amount) OVER (
                        ORDER BY timestamp
                        ROWS BETWEEN 30 PRECEDING AND 1 PRECEDING
                    ) AS avg_30d,
                    STDDEV(amount) OVER (
                        ORDER BY timestamp
                        ROWS BETWEEN 30 PRECEDING AND 1 PRECEDING
                    ) AS stddev_30d
                FROM transactions
                WHERE account_id = ?
                  AND timestamp >= NOW() - INTERVAL '90 days'
            )
            SELECT
                transaction_id,
                amount,
                timestamp,
                avg_30d,
                stddev_30d,
                (amount - avg_30d) / NULLIF(stddev_30d, 0) AS z_score
            FROM baseline
            WHERE stddev_30d IS NOT NULL
              AND amount > avg_30d + 2 * stddev_30d
            ORDER BY z_score DESC
        """;
        return jdbcTemplate.query(sql,
                new Object[]{accountId},
                (rs, rowNum) -> new AnomalyDto(
                        rs.getString("transaction_id"),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getBigDecimal("avg_30d"),
                        rs.getDouble("z_score")
                ));
    }
}