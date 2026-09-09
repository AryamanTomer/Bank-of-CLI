package com.bank.exception;

public class InsufficientFundsException extends BankingException {
    public InsufficientFundsException() {
        super("Insufficient funds. This account cannot be overdrawn.");
    }
}
