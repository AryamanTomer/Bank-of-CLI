package com.bank.exception;

// Errors we can actually show the user. getUserMessage() is the line that prints.
public class BankingException extends RuntimeException {
    private final String userMessage;

    public BankingException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }

    public BankingException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
