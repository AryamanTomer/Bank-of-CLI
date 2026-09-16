package com.bank.exception;

/** Shown when Postgres is down or a unique Account ID cannot be allocated. */
public class ServiceUnavailableException extends BankingException {
    public ServiceUnavailableException(Throwable cause) {
        super("Service temporarily unavailable. Please try again later.", cause);
    }
}
