package com.sportzone.util;

import java.util.regex.Pattern;

/**
 * Cung cap validation server-side dung chung cho cac form.
 */
public final class Validation {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$"
    );
    private static final Pattern VIETNAMESE_PHONE_PATTERN = Pattern.compile(
            "^(?:0|\\+84)(?:3|5|7|8|9)\\d{8}$"
    );

    private Validation() {
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static boolean isEmail(String value) {
        String normalized = trimToNull(value);
        return normalized != null && normalized.length() <= 100
                && EMAIL_PATTERN.matcher(normalized).matches();
    }

    public static boolean isVietnamesePhone(String value) {
        String normalized = trimToNull(value);
        return normalized != null && VIETNAMESE_PHONE_PATTERN.matcher(normalized).matches();
    }

    public static boolean isLengthBetween(String value, int minimum, int maximum) {
        String normalized = trimToNull(value);
        return normalized != null && normalized.length() >= minimum && normalized.length() <= maximum;
    }

    public static Integer parseIntegerInRange(String value, int minimum, int maximum) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(normalized);
            return parsed >= minimum && parsed <= maximum ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
