package com.bank.exception;

/** Thrown when an Account ID does not exist in the database. */
public class AccountNotFoundException extends BankingException {
    public AccountNotFoundException(String accountId) {
        super("No account was found for ID " + accountId + ".");
    }
}
