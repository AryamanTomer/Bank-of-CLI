package com.bank.util;

import org.mindrot.jbcrypt.BCrypt;

/** Hashes and verifies 4-digit PINs with BCrypt so the database never stores the PIN itself. */
public final class PinHasher {
    private PinHasher() {
    }

    public static String hash(String pin) {
        return BCrypt.hashpw(pin, BCrypt.gensalt());
    }

    public static boolean verify(String pin, String hash) {
        return BCrypt.checkpw(pin, hash);
    }
}
