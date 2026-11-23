package com.bank.fraud.audit.repository;

import com.bank.fraud.audit.entity.AlertDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AlertDocumentRepository extends ElasticsearchRepository<AlertDocument, String> {

    Page<AlertDocument> findByAccountId(String accountId, Pageable pageable);

    List<AlertDocument> findBySeverityAndDetectedAtAfter(
            String severity, Instant after);

    @Query("""
        {
          "bool": {
            "must": [
              { "term": { "accountId": "?0" } },
              { "range": { "detectedAt": { "gte": "?1" } } },
              { "range": { "riskScore": { "gte": "?2" } } }
            ]
          }
        }
        """)
    List<AlertDocument> searchByAccountAndScore(String accountId, Instant after, double minScore);
}