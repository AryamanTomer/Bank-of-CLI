package com.bank.domain;

import com.bank.util.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

// One account. pinHash is the BCrypt string, not the actual PIN.
public class Account {
    private final String accountId;
    private final String pinHash;
    private final BigDecimal balance;
    private final Instant createdAt;

    public Account(String accountId, String pinHash, BigDecimal balance, Instant createdAt) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.pinHash = Objects.requireNonNull(pinHash, "pinHash");
        this.balance = Money.scale(Objects.requireNonNull(balance, "balance"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public String getAccountId() {
        return accountId;
    }

    public String getPinHash() {
        return pinHash;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
