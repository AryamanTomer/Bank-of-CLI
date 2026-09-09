package com.bank.exception;

public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String accountId) {
        super("No account was found for ID " + accountId + ".");
    }
}
