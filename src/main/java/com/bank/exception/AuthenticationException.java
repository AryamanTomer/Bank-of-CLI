package com.bank.exception;

public class AuthenticationException extends BankingException {
    public AuthenticationException() {
        super("Incorrect Account ID or PIN. Please try again.");
    }
}
