package com.bank.exception;

// Bad input, like a PIN that isn't 4 digits.
public class ValidationException extends BankingException {
    public ValidationException(String userMessage) {
        super(userMessage);
    }
}
