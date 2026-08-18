package com.sportzone.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Tao va kiem tra CSRF token cho moi form POST. */
public final class CsrfUtil {

    public static final String PARAMETER_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() { }

    public static String ensureToken(HttpSession session) {
        Object current = session.getAttribute(AppConstants.SESSION_CSRF_TOKEN);
        if (current instanceof String && !((String) current).isEmpty()) {
            return (String) current;
        }

        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        session.setAttribute(AppConstants.SESSION_CSRF_TOKEN, token);
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(AppConstants.SESSION_CSRF_TOKEN);
        String actual = request.getParameter(PARAMETER_NAME);
        return expected instanceof String && actual != null
                && MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
                        actual.getBytes(StandardCharsets.UTF_8));
    }
}
