package com.sportzone.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class Validation {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_VN = Pattern.compile("^(0|\\+84)(3|5|7|8|9)\\d{8}$");

    private Validation() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static boolean isEmail(String value) {
        return !isBlank(value) && EMAIL.matcher(value.trim()).matches();
    }

    public static boolean isPhoneVn(String value) {
        if (isBlank(value)) {
            return false;
        }
        String compact = value.trim().replace(" ", "").replace("-", "");
        return PHONE_VN.matcher(compact).matches();
    }

    public static boolean maxLength(String value, int max) {
        return value == null || value.length() <= max;
    }

    public static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean notNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }
}
