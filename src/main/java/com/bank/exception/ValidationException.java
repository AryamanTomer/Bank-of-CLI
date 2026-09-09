package com.bank.exception;

public class ValidationException extends BankingException {
    public ValidationException(String userMessage) {
        super(userMessage);
    }
}
