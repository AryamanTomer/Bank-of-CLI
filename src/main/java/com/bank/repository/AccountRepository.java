package com.bank.repository;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.DataAccessException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.Account;
import com.bank.model.TransactionType;
import com.bank.util.ConnectionFactory;
import com.bank.util.Money;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class AccountRepository {
    public void create(Account account) {
        String sql = """
                INSERT INTO accounts (account_id, pin_hash, balance, created_at)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getAccountId());
            statement.setString(2, account.getPinHash());
            statement.setBigDecimal(3, Money.scale(account.getBalance()));
            statement.setTimestamp(4, Timestamp.from(account.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create account", e);
        }
    }

    public boolean existsById(String accountId) {
        String sql = "SELECT 1 FROM accounts WHERE account_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check account existence", e);
        }
    }

    public Optional<Account> findById(String accountId) {
        String sql = """
                SELECT account_id, pin_hash, balance, created_at
                FROM accounts
                WHERE account_id = ?
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Account(
                        resultSet.getString("account_id"),
                        resultSet.getString("pin_hash"),
                        resultSet.getBigDecimal("balance"),
                        resultSet.getTimestamp("created_at").toInstant()
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load account", e);
        }
    }

    public void deposit(String accountId, BigDecimal amount) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try {
                BigDecimal current = lockBalance(connection, accountId);
                updateBalance(connection, accountId, current.add(amount));
                insertTransaction(connection, accountId, TransactionType.DEPOSIT, amount, null, "Deposit");
                connection.commit();
            } catch (AccountNotFoundException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new DataAccessException("Failed to deposit", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to deposit", e);
        }
    }

    public void withdraw(String accountId, BigDecimal amount) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try {
                BigDecimal current = lockBalance(connection, accountId);
                if (current.compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                }
                updateBalance(connection, accountId, current.subtract(amount));
                insertTransaction(connection, accountId, TransactionType.WITHDRAWAL, amount, null, "Withdrawal");
                connection.commit();
            } catch (AccountNotFoundException | InsufficientFundsException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new DataAccessException("Failed to withdraw", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to withdraw", e);
        }
    }

    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String first = fromAccountId.compareTo(toAccountId) < 0 ? fromAccountId : toAccountId;
                String second = fromAccountId.compareTo(toAccountId) < 0 ? toAccountId : fromAccountId;
                BigDecimal firstBalance = lockBalance(connection, first);
                BigDecimal secondBalance = lockBalance(connection, second);
                BigDecimal fromBalance = fromAccountId.equals(first) ? firstBalance : secondBalance;
                if (fromBalance.compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                }
                BigDecimal toBalance = toAccountId.equals(first) ? firstBalance : secondBalance;

                updateBalance(connection, fromAccountId, fromBalance.subtract(amount));
                updateBalance(connection, toAccountId, toBalance.add(amount));
                insertTransaction(
                        connection,
                        fromAccountId,
                        TransactionType.TRANSFER_OUT,
                        amount,
                        toAccountId,
                        "Transfer to " + toAccountId
                );
                insertTransaction(
                        connection,
                        toAccountId,
                        TransactionType.TRANSFER_IN,
                        amount,
                        fromAccountId,
                        "Transfer from " + fromAccountId
                );
                connection.commit();
            } catch (AccountNotFoundException | InsufficientFundsException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new DataAccessException("Failed to transfer", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to transfer", e);
        }
    }

    private BigDecimal lockBalance(Connection connection, String accountId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new AccountNotFoundException(accountId);
                }
                return Money.scale(resultSet.getBigDecimal("balance"));
            }
        }
    }

    private void updateBalance(Connection connection, String accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, Money.scale(newBalance));
            statement.setString(2, accountId);
            statement.executeUpdate();
        }
    }

    private void insertTransaction(
            Connection connection,
            String accountId,
            TransactionType type,
            BigDecimal amount,
            String relatedAccountId,
            String description
    ) throws SQLException {
        String sql = """
                INSERT INTO transactions (account_id, type, amount, related_account_id, description)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            statement.setString(2, type.name());
            statement.setBigDecimal(3, Money.scale(amount));
            statement.setString(4, relatedAccountId);
            statement.setString(5, description);
            statement.executeUpdate();
        }
    }
}
