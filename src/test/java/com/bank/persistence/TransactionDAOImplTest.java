package com.bank.persistence;

import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionDAOImplTest {
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();

    @BeforeEach
    void cleanTestDatabase() throws Exception {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM transactions");
            statement.execute("DELETE FROM accounts");
        }
    }

    @Test
    void findRecentByAccountId_returnsNewestTransactions() {
        accountDAO.create(new Account("10000001", "hash", new BigDecimal("0.00"), Instant.now()));
        accountDAO.deposit("10000001", new BigDecimal("25.00"));

        List<Transaction> history = transactionDAO.findRecentByAccountId("10000001", 20);

        assertEquals(1, history.size());
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(new BigDecimal("25.00"), history.get(0).getAmount());
    }

    @Test
    void findRecentByAccountId_returnsEmptyWhenNoneExist() {
        accountDAO.create(new Account("10000001", "hash", new BigDecimal("0.00"), Instant.now()));

        List<Transaction> history = transactionDAO.findRecentByAccountId("10000001", 20);

        assertTrue(history.isEmpty());
    }
}
