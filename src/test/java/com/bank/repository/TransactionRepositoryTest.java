package com.bank.repository;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.util.ConnectionFactory;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRepositoryTest {
    private final AccountRepository accountRepository = new AccountRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();

    @BeforeEach
    void cleanTestDatabase() throws Exception {
        try (Connection connection = ConnectionFactory.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM transactions");
            statement.execute("DELETE FROM accounts");
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "bank_cli_test is not available: " + e.getMessage());
        }
    }

    @Test
    void findRecentByAccountId_returnsNewestTransactions() {
        accountRepository.create(new Account("10000001", "hash", new BigDecimal("0.00"), Instant.now()));
        accountRepository.deposit("10000001", new BigDecimal("25.00"));

        List<Transaction> history = transactionRepository.findRecentByAccountId("10000001", 20);

        assertEquals(1, history.size());
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(new BigDecimal("25.00"), history.get(0).getAmount());
    }

    @Test
    void findRecentByAccountId_returnsEmptyWhenNoneExist() {
        accountRepository.create(new Account("10000001", "hash", new BigDecimal("0.00"), Instant.now()));

        List<Transaction> history = transactionRepository.findRecentByAccountId("10000001", 20);

        assertTrue(history.isEmpty());
    }
}
