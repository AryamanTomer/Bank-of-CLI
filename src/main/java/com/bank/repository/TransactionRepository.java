package com.bank.repository;

import com.bank.domain.Transaction;

import java.util.List;

// Just reading history. Deposits/withdrawals/transfers write the rows in AccountRepositoryImpl.
public interface TransactionRepository {
    List<Transaction> findRecentByAccountId(String accountId, int limit);
}
