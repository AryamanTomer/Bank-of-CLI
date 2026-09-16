package com.bank.exception;

/** Wrong Account ID or PIN. The message is intentionally the same in both cases. */
public class AuthenticationException extends BankingException {
    public AuthenticationException() {
        super("Incorrect Account ID or PIN. Please try again.");
    }
}
