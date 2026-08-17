package com.sportzone.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 hex (UTF-8, no salt) — matches sql/02 seed hashes.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String sha256Hex(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("password is required");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    public static boolean matches(String raw, String expectedHex) {
        if (raw == null || expectedHex == null) {
            return false;
        }
        return sha256Hex(raw).equalsIgnoreCase(expectedHex);
    }

    private static String toHex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        final char[] digits = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            hex[i * 2] = digits[value >>> 4];
            hex[i * 2 + 1] = digits[value & 0x0f];
        }
        return new String(hex);
    }
}
