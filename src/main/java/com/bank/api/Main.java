package com.bank.api;

import com.bank.repository.AccountRepository;
import com.bank.repository.AccountRepositoryImpl;
import com.bank.repository.TransactionRepository;
import com.bank.repository.TransactionRepositoryImpl;
import com.bank.service.AccountService;
import com.bank.service.AccountServiceImpl;
import com.bank.util.AppLogger;

import java.util.Scanner;

/**
 * Application entry point. Wires repository → service → CLI, then starts the terminal menus.
 * The CLI never talks to JDBC directly; it only receives {@link AccountService}.
 */
public class Main {
    public static void main(String[] args) {
        try {
            AccountRepository accountRepository = new AccountRepositoryImpl();
            TransactionRepository transactionRepository = new TransactionRepositoryImpl();
            AccountService service = new AccountServiceImpl(accountRepository, transactionRepository);
            try (Scanner scanner = new Scanner(System.in)) {
                new BankCli(service, scanner).start();
            }
        } catch (IllegalStateException e) {
            // ConnectionFactory throws this when Postgres cannot be reached at startup.
            AppLogger.error("Database connection lost", e);
            System.out.println("Service temporarily unavailable. Please try again later.");
        }
    }
}
