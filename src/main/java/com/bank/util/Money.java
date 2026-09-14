package com.bank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class Money {
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);

    private Money() {
    }

    public static BigDecimal scale(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static String format(BigDecimal amount) {
        return CURRENCY.format(scale(amount));
    }
}
