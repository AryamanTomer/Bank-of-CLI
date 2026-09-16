package com.bank.exception;

/** Input that fails a business rule before any money is moved, such as a PIN that is not 4 digits. */
public class ValidationException extends BankingException {
    public ValidationException(String userMessage) {
        super(userMessage);
    }
}
