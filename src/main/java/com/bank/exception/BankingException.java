package com.bank.exception;

/**
 * Base type for errors the CLI can show to the user. {@link #getUserMessage()} is the text printed on screen.
 */
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
