package com.bank.repository;

import com.bank.domain.Transaction;

import java.util.List;

/**
 * Read-only repository for ledger rows. Writes happen inside {@link AccountRepository} money movements.
 */
public interface TransactionRepository {
    List<Transaction> findRecentByAccountId(String accountId, int limit);
}
