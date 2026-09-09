package com.bank.util;

import org.mindrot.jbcrypt.BCrypt;

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
