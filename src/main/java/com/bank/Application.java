package com.bank;

import com.bank.api.BankCli;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.AccountService;
import com.bank.util.AppLogger;
import com.bank.util.ConnectionFactory;

import java.sql.Connection;
import java.util.Scanner;

public final class Application {
    private Application() {
    }

    public static void main(String[] args) {
        try (Connection ignored = ConnectionFactory.getConnection()) {
            // Confirm PostgreSQL is ready before showing the menu.
        } catch (Exception e) {
            AppLogger.error("Database connection lost", e);
            System.out.println("Service temporarily unavailable. Please try again later.");
            return;
        }

        AccountService accountService = new AccountService(new AccountRepository(), new TransactionRepository());
        try (Scanner scanner = new Scanner(System.in)) {
            new BankCli(accountService, scanner).start();
        }
    }
}
