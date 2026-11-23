# Fraud Detection Engine

Real-time transaction fraud detection platform using event-driven microservices architecture.

## Architecture
┌─────────────────────────────────────────────────────────────────────────────────┐
│ FRAUD DETECTION ENGINE │
├─────────────────────────────────────────────────────────────────────────────────┤
│ │
│ ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐ │
│ │ transaction- │ │ scoring- │ │ audit- │ │
│ │ service │ │ service │ │ service │ │
│ │ │ │ │ │ │ │
│ │ • REST API │ │ • Kafka Consumer│ │ • Kafka Consumer│ │
│ │ • PostgreSQL │ │ • Rule Engine │ │ • Elasticsearch │ │
│ │ • Kafka Producer│ │ • Redis Cache │ │ • Search API │ │
│ │ • Advanced SQL │ │ • Kafka Producer│ │ │ │
│ └───────┬──────────┘ └───────┬──────────┘ └───────┬──────────┘ │
│ │ │ │ │
│ ▼ ▼ ▼ │
│ ┌─────────────────────────────────────────────────────────────────────────┐ │
│ │ KAFKA (KRaft Mode) │ │
│ │ │ │
│ │ Topic: "transactions" ──→ Topic: "alerts" ──→ Topic: "alerts.DLT"│ │
│ └─────────────────────────────────────────────────────────────────────────┘ │
│ │
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │
│ │ PostgreSQL │ │ Redis │ │ Elasticsearch │ │
│ │ (16) │ │ (7) │ │ (8.13) │ │
│ │ │ │ │ │ │ │
│ │ • Transaction│ │ • Sliding │ │ • Alert Index │ │
│ │ History │ │ Window │ │ • Full-text │ │
│ │ • Window │ │ • Baseline │ │ Search │ │
│ │ Functions │ │ Cache │ │ • Aggregations │ │
│ │ • CTEs │ │ • Velocity │ │ │ │
│ └──────────────┘ └──────────────┘ └──────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘


## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 (Virtual Threads) |
| Framework | Spring Boot 3.3.x |
| Build | Maven (multi-module) |
| Messaging | Apache Kafka 3.9 (KRaft) |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Search | Elasticsearch 8.13 |
| Migrations | Flyway |
| Containerization | Docker + Docker Compose |

## Microservices

| Service | Port | Responsibility |
|---------|------|---------------|
| `transaction-service` | 8081 | Ingest transactions, persist to Postgres, publish to Kafka, advanced SQL analytics |
| `scoring-service` | 8082 | Consume events, apply fraud rules (Strategy pattern), score & alert |
| `audit-service` | 8083 | Consume alerts, index to Elasticsearch, expose search API |

## Fraud Rules

| Rule | Mechanism | Data Store |
|------|-----------|-----------|
| **VELOCITY** | ≥5 transactions in 10-min sliding window | Redis Sorted Set |
| **AMOUNT_ANOMALY** | Amount > 3× category baseline | Redis (cache-aside) |
| **MERCHANT_PATTERN** | ≥4 unique merchants in 1 hour | Redis Sorted Set |

## Getting Started

### Prerequisites

- Docker & Docker Compose
- JDK 21 (for local dev)
- Maven 3.9+

### Quick Start (Docker)

```bash
# Build & start everything
docker compose up --build -d

# Wait for health
docker compose ps

# Local development
# 1. Start infrastructure only
docker compose up -d postgres redis kafka elasticsearch

# 2. Build all modules
mvn clean install -DskipTests

# 3. Run services (in separate terminals)
cd transaction-service && mvn spring-boot:run
cd scoring-service && mvn spring-boot:run
cd audit-service && mvn spring-boot:run

API Endpoints
Transaction Service (port 8081)
Method	Endpoint	Description
POST	/api/transactions	Create new transaction
GET	/api/transactions/{accountId}	List transactions by account
GET	/api/transactions/{accountId}/running-balance?from=2024-01-01&to=2024-12-31	Running balance (window function)
GET	/api/transactions/{accountId}/category-spend?days=30	Avg spend by category (CTE)
GET	/api/transactions/{accountId}/anomalies	Z-score anomaly detection

Audit Service (port 8083)
Method	Endpoint	Description
GET	/api/audit/alerts/{accountId}?page=0&size=20	Paginated alerts by account
GET	/api/audit/alerts/{accountId}/high-risk?since=2024-01-01T00:00:00Z&minScore=0.5	High-risk alerts
GET	/api/audit/alerts/critical?since=2024-01-01T00:00:00Z

Example Requests
Create a Transaction
curl -X POST http://localhost:8081/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC-001",
    "merchantId": "M-100",
    "merchantName": "Luxury Watch Store",
    "category": "JEWELRY",
    "amount": 50000.00,
    "currency": "USD",
    "channel": "ONLINE",
    "countryCode": "US"
  }'

Check Running Balance (Window Function)
curl "http://localhost:8081/api/transactions/ACC-001/running-balance?from=2024-01-01&to=2024-12-31"

Search Alerts in Elasticsearch
curl -X GET "http://localhost:8083/api/audit/alerts/ACC-001/high-risk?since=2024-01-01T00:00:00Z&minScore=0.5"

Direct ES Query (bonus)
curl -X GET "http://localhost:9200/fraud-alerts/_search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must": [
          { "term": { "severity": "CRITICAL" } }
        ]
      }
    },
    "sort": [{ "detectedAt": "desc" }]
  }'

Stop & Cleanup
docker compose down          # Stop services
docker compose down -v       # Stop + remove volumes (data loss!)


---

## Ringkasan File yang Sudah di-Generate

| Module | Files |
|--------|-------|
| **shared** | `pom.xml`, `TransactionEvent.java`, `AlertEvent.java`, `TransactionDto.java`, `TransactionResponseDto.java` |
| **transaction-service** | `pom.xml`, `Dockerfile`, `Application`, `KafkaConfig`, `RedisConfig`, `Transaction` entity, `TransactionRepository` + `Custom` + `CustomImpl`, 3 DTOs, `TransactionService`, `TransactionEventPublisher`, `TransactionController`, `application.yml`, `V1__init.sql` |
| **scoring-service** | `pom.xml`, `Dockerfile`, `Application`, `KafkaConfig`, `RedisConfig`, `FraudRule` interface, `VelocityRule`, `AmountAnomalyRule`, `MerchantPatternRule`, `FraudScoringEngine`, `ScoringResult`, `TransactionEventConsumer`, `AlertPublisher`, `ScoringHealthController`, `application.yml` |
| **audit-service** | `pom.xml`, `Dockerfile`, `Application`, `KafkaConfig`, `ElasticsearchConfig`, `AlertDocument`, `es-settings.json`, `es-mapping.json`, `AlertDocumentRepository`, `AlertEventConsumer`, `AuditSearchController`, `application.yml` |
| **Root** | `docker-compose.yml`, `README.md` |

Semua file sudah lengkap. Tinggal `mvn clean install` + `docker compose up --bu