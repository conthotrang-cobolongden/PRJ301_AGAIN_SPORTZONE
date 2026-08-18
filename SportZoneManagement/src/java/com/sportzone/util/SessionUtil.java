package com.sportzone.util;

import com.sportzone.model.Account;
import javax.servlet.http.HttpSession;

/** Quan ly contract session dung chung cho auth va JSP. Tag: A1, A2, U2, U3. */
public final class SessionUtil {

    private SessionUtil() { }

    public static void storeLoggedInAccount(HttpSession session, Account account) {
        if (session == null || account == null) {
            throw new IllegalArgumentException("Session and account must not be null");
        }

        // Khong luu hash trong session vi session co the bi serialize hoac dump log.
        account.setPasswordHash(null);
        session.setAttribute(AppConstants.SESSION_ACCOUNT, account);
        session.setAttribute(AppConstants.SESSION_ROLE_NAME, account.getRoleName());
        session.setAttribute(AppConstants.SESSION_FROZEN,
                AppConstants.ACCOUNT_FROZEN.equals(account.getStatus()));
        CsrfUtil.ensureToken(session);
    }

    public static Account getLoggedInAccount(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(AppConstants.SESSION_ACCOUNT);
        return value instanceof Account ? (Account) value : null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getLoggedInAccount(session) != null;
    }

    public static boolean isAdmin(HttpSession session) {
        return session != null && AppConstants.ROLE_ADMIN.equals(
                session.getAttribute(AppConstants.SESSION_ROLE_NAME));
    }

    public static boolean isFrozen(HttpSession session) {
        return session != null && Boolean.TRUE.equals(
                session.getAttribute(AppConstants.SESSION_FROZEN));
    }
}
