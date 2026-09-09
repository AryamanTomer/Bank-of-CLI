CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(16) PRIMARY KEY,
    pin_hash VARCHAR(60) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id VARCHAR(16) NOT NULL REFERENCES accounts(account_id),
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    related_account_id VARCHAR(16),
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_created
    ON transactions (account_id, created_at DESC);
