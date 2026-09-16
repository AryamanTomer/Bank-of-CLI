package com.bank.exception;

/** Amounts must be greater than zero and have at most two decimal places. */
public class InvalidAmountException extends BankingException {
    public InvalidAmountException() {
        super("Amount must be greater than zero.");
    }

    public InvalidAmountException(String userMessage) {
        super(userMessage);
    }
}
