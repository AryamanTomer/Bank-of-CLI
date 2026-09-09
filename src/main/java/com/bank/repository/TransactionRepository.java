package com.bank.repository;

import com.bank.exception.DataAccessException;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepository {
    public List<Transaction> findRecentByAccountId(String accountId, int limit) {
        String sql = """
                SELECT id, account_id, type, amount, related_account_id, description, created_at
                FROM transactions
                WHERE account_id = ?
                ORDER BY created_at DESC, id DESC
                LIMIT ?
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountId);
            statement.setInt(2, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                while (resultSet.next()) {
                    Instant createdAt = Optional.ofNullable(resultSet.getTimestamp("created_at"))
                            .map(timestamp -> timestamp.toInstant())
                            .orElse(Instant.EPOCH);
                    transactions.add(new Transaction(
                            resultSet.getLong("id"),
                            resultSet.getString("account_id"),
                            TransactionType.valueOf(resultSet.getString("type")),
                            resultSet.getBigDecimal("amount"),
                            resultSet.getString("related_account_id"),
                            resultSet.getString("description"),
                            createdAt
                    ));
                }
                return List.copyOf(transactions);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load transaction history", e);
        }
    }
}
