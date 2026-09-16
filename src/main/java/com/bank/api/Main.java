package com.bank.api;

import com.bank.persistence.AccountDAO;
import com.bank.persistence.AccountDAOImpl;
import com.bank.persistence.TransactionDAO;
import com.bank.persistence.TransactionDAOImpl;
import com.bank.service.AccountService;
import com.bank.service.AccountServiceImpl;
import com.bank.util.AppLogger;

import java.util.Scanner;

/**
 * Application entry point. Wires DAO → service → CLI, then starts the terminal menus.
 * The CLI never talks to JDBC directly; it only receives {@link AccountService}.
 */
public class Main {
    public static void main(String[] args) {
        try {
            AccountDAO accountDAO = new AccountDAOImpl();
            TransactionDAO transactionDAO = new TransactionDAOImpl();
            AccountService service = new AccountServiceImpl(accountDAO, transactionDAO);
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
