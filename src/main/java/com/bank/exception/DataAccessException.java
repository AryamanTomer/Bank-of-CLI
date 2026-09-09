package com.bank.exception;

public class DataAccessException extends BankingException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
