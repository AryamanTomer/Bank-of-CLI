package com.bank.exception;

// Wrong ID or wrong PIN. Same message either way.
public class AuthenticationException extends BankingException {
    public AuthenticationException() {
        super("Incorrect Account ID or PIN. Please try again.");
    }
}
