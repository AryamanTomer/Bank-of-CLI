package com.bank.util;

import java.security.SecureRandom;

/** Builds an 8-digit Account ID that does not start with zero. */
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
