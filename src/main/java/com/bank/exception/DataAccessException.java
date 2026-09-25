package com.bank.exception;

// JDBC blew up. Don't show this text in the CLI — log it and say service unavailable.
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
