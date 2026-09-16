package com.bank.exception;

/**
 * JDBC failure. This is not a {@link BankingException} so the CLI never prints a raw SQL message.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
