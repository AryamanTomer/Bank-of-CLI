package com.bank.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Money {
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);

    private Money() {
    }

    public static String format(BigDecimal amount) {
        return CURRENCY.format(amount);
    }
}
