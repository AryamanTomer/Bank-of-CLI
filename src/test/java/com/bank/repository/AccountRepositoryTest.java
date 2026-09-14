package com.bank.repository;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DataAccessException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.util.ConnectionFactory;
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

class AccountRepositoryTest {
    private final AccountRepository repository = new AccountRepository();

    @BeforeEach
    void cleanTestDatabase() throws Exception {
        try (Connection connection = ConnectionFactory.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM transactions");
            statement.execute("DELETE FROM accounts");
        }
    }

    @Test
    void create_insertsAccount() {
        repository.create(account("10000001"));

        assertTrue(repository.existsById("10000001"));
        assertEquals("10000001", repository.findById("10000001").orElseThrow().getAccountId());
        assertEquals(new BigDecimal("0.00"), repository.findById("10000001").orElseThrow().getBalance());
    }

    @Test
    void create_rejectsDuplicateAccountId() {
        repository.create(account("10000001"));

        assertThrows(DataAccessException.class, () -> repository.create(account("10000001")));
    }

    @Test
    void existsById_returnsTrueWhenAccountExists() {
        repository.create(account("10000001"));

        assertTrue(repository.existsById("10000001"));
    }

    @Test
    void existsById_returnsFalseWhenAccountIsMissing() {
        assertFalse(repository.existsById("99999999"));
    }

    @Test
    void findById_returnsAccountWhenItExists() {
        repository.create(account("10000001"));

        Account found = repository.findById("10000001").orElseThrow();
        assertEquals("10000001", found.getAccountId());
    }

    @Test
    void findById_returnsEmptyWhenMissing() {
        assertTrue(repository.findById("99999999").isEmpty());
    }

    @Test
    void deposit_addsFundsAndRecordsTransaction() throws Exception {
        repository.create(account("10000001"));

        repository.deposit("10000001", new BigDecimal("25.00"));

        assertEquals(new BigDecimal("25.00"), repository.findById("10000001").orElseThrow().getBalance());
        try (Connection connection = ConnectionFactory.getConnection();
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
                () -> repository.deposit("10000001", new BigDecimal("25.00")));
    }

    @Test
    void withdraw_subtractsFundsAndRecordsTransaction() throws Exception {
        repository.create(account("10000001", "40.00"));

        repository.withdraw("10000001", new BigDecimal("15.00"));

        assertEquals(new BigDecimal("25.00"), repository.findById("10000001").orElseThrow().getBalance());
        try (Connection connection = ConnectionFactory.getConnection();
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
        repository.create(account("10000001", "10.00"));

        assertThrows(InsufficientFundsException.class,
                () -> repository.withdraw("10000001", new BigDecimal("10.01")));
        assertEquals(new BigDecimal("10.00"), repository.findById("10000001").orElseThrow().getBalance());
    }

    @Test
    void transfer_movesFundsAtomically() {
        repository.create(account("10000001", "100.00"));
        repository.create(account("10000002", "20.00"));

        repository.transfer("10000001", "10000002", new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), repository.findById("10000001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("50.00"), repository.findById("10000002").orElseThrow().getBalance());
    }

    @Test
    void transfer_rollsBackWhenSourceCannotCoverAmount() {
        repository.create(account("10000001", "10.00"));
        repository.create(account("10000002", "50.00"));

        assertThrows(InsufficientFundsException.class,
                () -> repository.transfer("10000001", "10000002", new BigDecimal("25.00")));
        assertEquals(new BigDecimal("10.00"), repository.findById("10000001").orElseThrow().getBalance());
        assertEquals(new BigDecimal("50.00"), repository.findById("10000002").orElseThrow().getBalance());
    }

    private Account account(String id) {
        return account(id, "0.00");
    }

    private Account account(String id, String balance) {
        return new Account(id, "hash", new BigDecimal(balance), Instant.now());
    }
}
