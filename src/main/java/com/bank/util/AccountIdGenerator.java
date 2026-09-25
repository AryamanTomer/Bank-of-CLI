package com.bank.util;

import java.security.SecureRandom;

// 8-digit IDs. First digit isn't 0 so they don't look like 01234567.
public final class AccountIdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private AccountIdGenerator() {
    }

    public static String nextId() {
        StringBuilder id = new StringBuilder(8);
        id.append(RANDOM.nextInt(9) + 1);
        for (int i = 1; i < 8; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }
}
