package com.bank.persistence;

import com.bank.domain.Account;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DataAccessException;
import com.bank.exception.InsufficientFundsException;
import com.bank.util.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JDBC tests against {@code bank_cli_test}. Surefire sets {@code bank.test.db=true}
 * so these methods never touch the live {@code bank_cli} database.
 */
class AccountDAOImplTest {
    private final AccountDAO dao = new AccountDAOImpl();

    @BeforeEach
    void cleanTestDatabase() throws Exception {
        // Wipe rows between tests so assertions do not depend on leftover data.
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM transactions");
            statement.execute("DELETE FROM accounts");
        }
    }

    @Test
    void create_insertsAccount() {
        dao.create(account("10000001"));

        assertTrue(dao.existsById("10000001"));
        assertEquals("10000001", dao.findById("10000001").orElseThrow().getAccountId());
        assertEquals(new BigDecimal("0.00"), dao.findById("10000001").orElseThrow().getBalance());
    }

    @Test
    void create_rejectsDuplicateAccountId() {
        dao.create(account("10000001"));

        assertThrows(DataAccessException.class, () -> dao.create(account("10000001")));
    }

    @Test
    void existsById_returnsTrueWhenAccountExists() {
        dao.create(account("10000001"));

        assertTrue(dao.existsById("10000001"));
    }

    @Test
    void existsById_returnsFalseWhenAccountIsMissing() {
        assertFalse(dao.existsById("99999999"));
    }

    @Test
    void findById_returnsAccountWhenItExists() {
        dao.create(account("10000001"));

        Account found = dao.findById("10000001").orElseThrow();
        assertEquals("10000001", found.getAccountId());
    }

    @Test
    void findById_returnsEmptyWhenMissing() {
        assertTrue(dao.findById("99999999").isEmpty());
    }

    @Test
    void deposit_addsFundsAndRecordsTransaction() throws Exception {
        dao.create(account("10000001"));

        dao.deposit("10000001", new BigDecimal("25.00"));

        assertEquals(new BigDecimal("25.00"), dao.findById("10000001").orElseThrow().getBalance());
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
             Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery(
                     "SELECT type, amount FROM transactions WHERE account_id = '10000001'"
             )) {
            assertTrue(resultSet.next());
            assertEquals("DEPOSIT", resultSet.getString("type"));
            assertEquals(new BigDecimal("25.00"), Money.scale(resultSet.getBigDecimal("amount")));
        }
    }

    @Test
    void deposit_rejectsUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> dao.deposit("10000001", new BigDecimal("25.00")));
    }

    @Test
    void withdraw_subtractsFundsAndRecordsTransaction() throws Exception {
        dao.create(account("10000001", "40.00"));

        dao.withdraw("10000001", new BigDecimal("15.00"));

        assertEquals(new BigDecimal("25.00"), dao.findById("10000001").orElseThrow().getBalance());
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
             Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery(
                     "SELECT type, amount FROM transactions WHERE account_id = '10000001'"
             )) {
            assertTrue(resultSet.next());
            assertEquals("WITHDRAWAL", resultSet.getString("type"));
            assertEquals(new BigDecimal("15.00"), Money.scale(resultSet.getBigDecimal("amount")));
        }
    }

    @Test
    void withdraw_rejectsOverdraftAndLeavesBalanceUnchanged() {
        dao.create(account("10000001", "10.00"));

        assertThrows(InsufficientFundsException.class,
                () -> dao.withdraw("10000001", new BigDecimal("10.01")));
        assertEquals(new BigDecimal("10.00"), dao.findById("10000001").orElseThrow().getBalance());
    }

    @Test
    void transfer_movesFundsAtomically() {
        dao.create(account("10000001", "100.00"));
        dao.create(account("10000002", "20.00"));

        dao.transfer("10000001", "10000002", new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), dao.findById("10000001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("50.00"), dao.findById("10000002").orElseThrow().getBalance());
    }

    @Test
    void transfer_rollsBackWhenSourceCannotCoverAmount() {
        dao.create(account("10000001", "10.00"));
        dao.create(account("10000002", "50.00"));

        assertThrows(InsufficientFundsException.class,
                () -> dao.transfer("10000001", "10000002", new BigDecimal("25.00")));
        assertEquals(new BigDecimal("10.00"), dao.findById("10000001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("50.00"), dao.findById("10000002").orElseThrow().getBalance());
    }

    private Account account(String id) {
        return account(id, "0.00");
    }

    private Account account(String id, String balance) {
        return new Account(id, "hash", new BigDecimal(balance), Instant.now());
    }
}
