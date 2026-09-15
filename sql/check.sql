-- Run against bank_cli or bank_cli_test on localhost:5432
SELECT current_database();

SELECT account_id, balance, created_at
FROM accounts
ORDER BY account_id;

SELECT id, account_id, type, amount, related_account_id, created_at
FROM transactions
ORDER BY id DESC
LIMIT 20;
