package com.bank.persistence;

import com.bank.domain.Transaction;

import java.util.List;

/**
 * Read-only persistence API for ledger rows. Writes happen inside {@link AccountDAO} money movements.
 */
public interface TransactionDAO {
    List<Transaction> findRecentByAccountId(String accountId, int limit);
}
