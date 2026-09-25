package com.bank.exception;

// Zero, negative, or more than two decimal places.
public class InvalidAmountException extends BankingException {
    public InvalidAmountException() {
        super("Amount must be greater than zero.");
    }

    public InvalidAmountException(String userMessage) {
        super(userMessage);
    }
}
