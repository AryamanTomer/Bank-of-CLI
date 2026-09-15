package com.bank.service;

import com.bank.domain.Account;
import com.bank.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account register(String pin);

    Account login(String accountId, String pin);

    BigDecimal getBalance(String accountId);

    void deposit(String accountId, BigDecimal amount);

    void withdraw(String accountId, BigDecimal amount);

    void transfer(String fromAccountId, String toAccountId, BigDecimal amount);

    List<Transaction> getHistory(String accountId);
}
