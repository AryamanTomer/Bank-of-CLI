package com.bank.persistence;

import com.bank.domain.Transaction;

import java.util.List;

public interface TransactionDAO {
    List<Transaction> findRecentByAccountId(String accountId, int limit);
}
