package com.bank.service;

import com.bank.domain.Account;
import com.bank.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Banking use cases exposed to the API layer. All PIN, overdraft, and amount rules live here.
 */
public interface AccountService {
    /** Creates an account with a unique 8-digit ID and a hashed PIN. */
    Account register(String pin);

    /** Verifies Account ID and PIN. Throws if either is wrong so callers cannot tell them apart. */
    Account login(String accountId, String pin);

    BigDecimal getBalance(String accountId);

    void deposit(String accountId, BigDecimal amount);

    void withdraw(String accountId, BigDecimal amount);

    void transfer(String fromAccountId, String toAccountId, BigDecimal amount);

    /** Returns the most recent transactions for the account, newest first. */
    List<Transaction> getHistory(String accountId);
}
