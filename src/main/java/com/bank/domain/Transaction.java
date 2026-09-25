package com.bank.domain;

import com.bank.util.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

// One line in the history. A transfer writes two of these: out on one account, in on the other.
public class Transaction {
    private final Long id;
    private final String accountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String relatedAccountId;
    private final String description;
    private final Instant createdAt;

    public Transaction(
            Long id,
            String accountId,
            TransactionType type,
            BigDecimal amount,
            String relatedAccountId,
            String description,
            Instant createdAt
    ) {
        this.id = id;
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.type = Objects.requireNonNull(type, "type");
        this.amount = Money.scale(Objects.requireNonNull(amount, "amount"));
        this.relatedAccountId = relatedAccountId;
        this.description = description;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Set on transfers. Empty for a regular deposit or withdrawal.
    public Optional<String> getRelatedAccountId() {
        return Optional.ofNullable(relatedAccountId);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
