package com.bank.exception;

public class ServiceUnavailableException extends BankingException {
    public ServiceUnavailableException(Throwable cause) {
        super("Service temporarily unavailable. Please try again later.", cause);
    }
}
