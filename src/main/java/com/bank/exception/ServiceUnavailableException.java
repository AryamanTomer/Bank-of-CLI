package com.bank.exception;

// DB is down, or we couldn't mint a unique Account ID.
public class ServiceUnavailableException extends BankingException {
    public ServiceUnavailableException(Throwable cause) {
        super("Service temporarily unavailable. Please try again later.", cause);
    }
}
