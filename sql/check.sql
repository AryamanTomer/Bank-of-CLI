-- Inspect the embedded Postgres instance while the app is running.
-- App: localhost:55432  (data/pg)
-- Tests: localhost:55433 (data/pg-test)
-- User: postgres   Database: postgres   Password: (empty)

SELECT account_id, balance, created_at
FROM accounts
ORDER BY account_id;

SELECT id, account_id, type, amount, related_account_id, created_at
FROM transactions
ORDER BY id DESC
LIMIT 20;
