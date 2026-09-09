package com.bank.exception;

public class InvalidAmountException extends BankingException {
    public InvalidAmountException() {
        super("Amount must be greater than zero.");
    }

    public InvalidAmountException(String userMessage) {
        super(userMessage);
    }
}
