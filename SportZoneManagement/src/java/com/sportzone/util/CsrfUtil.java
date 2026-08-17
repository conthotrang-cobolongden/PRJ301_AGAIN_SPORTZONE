package com.sportzone.util;

import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class CsrfUtil {

    public static final String SESSION_KEY = "csrfToken";

    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() {
    }

    public static String ensureToken(HttpSession session) {
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null || token.isEmpty()) {
            token = nextToken();
            session.setAttribute(SESSION_KEY, token);
        }
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute(SESSION_KEY);
        String actual = request.getParameter(AppConstants.CSRF_PARAM);
        return expected != null && expected.equals(actual);
    }

    private static String nextToken() {
        byte[] buffer = new byte[16];
        RANDOM.nextBytes(buffer);
        StringBuilder hex = new StringBuilder(32);
        for (byte b : buffer) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
