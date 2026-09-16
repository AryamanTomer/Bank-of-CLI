package com.bank.exception;

/** Withdrawal or transfer that would take the balance below zero. */
public class InsufficientFundsException extends BankingException {
    public InsufficientFundsException() {
        super("Insufficient funds. This account cannot be overdrawn.");
    }
}
