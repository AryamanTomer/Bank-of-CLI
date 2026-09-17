package com.bank.repository;

import com.bank.domain.Account;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Repository API for accounts. Implementations own SQL and JDBC; the service never writes SQL.
 */
public interface AccountRepository {
    void create(Account account);

    boolean existsById(String accountId);

    Optional<Account> findById(String accountId);

    void deposit(String accountId, BigDecimal amount);

    void withdraw(String accountId, BigDecimal amount);

    /** Moves funds between two accounts in one database transaction. */
    void transfer(String fromAccountId, String toAccountId, BigDecimal amount);
}
