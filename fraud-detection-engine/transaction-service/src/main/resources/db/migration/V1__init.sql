CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id   UUID PRIMARY KEY,
                                            account_id       VARCHAR(64) NOT NULL,
    merchant_id      VARCHAR(64) NOT NULL,
    merchant_name    VARCHAR(255) NOT NULL,
    category         VARCHAR(64) NOT NULL,
    amount           DECIMAL(15, 2) NOT NULL,
    currency         VARCHAR(3) NOT NULL DEFAULT 'USD',
    channel          VARCHAR(16) NOT NULL,
    country_code     VARCHAR(2),
    timestamp        TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_timestamp ON transactions(timestamp);
CREATE INDEX IF NOT EXISTS idx_account_timestamp ON transactions(account_id, timestamp);

-- Seed sample data for demo
INSERT INTO transactions (transaction_id, account_id, merchant_id, merchant_name, category, amount, currency, channel, country_code, timestamp)
VALUES
    (gen_random_uuid(), 'ACC-001', 'M-001', 'Grocery Store A', 'GROCERY', 45.50, 'USD', 'POS', 'US', NOW() - INTERVAL '1 day'),
    (gen_random_uuid(), 'ACC-001', 'M-002', 'Coffee Shop B', 'FOOD', 5.20, 'USD', 'POS', 'US', NOW() - INTERVAL '1 day'),
    (gen_random_uuid(), 'ACC-001', 'M-003', 'Tech Store C', 'ELECTRONICS', 299.99, 'USD', 'ONLINE', 'US', NOW() - INTERVAL '2 days'),
    (gen_random_uuid(), 'ACC-001', 'M-001', 'Grocery Store A', 'GROCERY', 52.30, 'USD', 'POS', 'US', NOW() - INTERVAL '3 days'),
    (gen_random_uuid(), 'ACC-001', 'M-004', 'Restaurant D', 'FOOD', 85.00, 'USD', 'POS', 'US', NOW() - INTERVAL '4 days'),
    (gen_random_uuid(), 'ACC-001', 'M-005', 'Travel Agency E', 'TRAVEL', 1200.00, 'USD', 'ONLINE', 'US', NOW() - INTERVAL '5 days'),
    (gen_random_uuid(), 'ACC-001', 'M-001', 'Grocery Store A', 'GROCERY', 48.75, 'USD', 'POS', 'US', NOW() - INTERVAL '6 days'),
    (gen_random_uuid(), 'ACC-001', 'M-002', 'Coffee Shop B', 'FOOD', 6.10, 'USD', 'POS', 'US', NOW() - INTERVAL '7 days'),
    (gen_random_uuid(), 'ACC-002', 'M-006', 'Pharmacy F', 'HEALTH', 35.00, 'USD', 'POS', 'US', NOW() - INTERVAL '1 day'),
    (gen_random_uuid(), 'ACC-002', 'M-007', 'Gas Station G', 'TRANSPORT', 40.00, 'USD', 'POS', 'US', NOW() - INTERVAL '2 days');