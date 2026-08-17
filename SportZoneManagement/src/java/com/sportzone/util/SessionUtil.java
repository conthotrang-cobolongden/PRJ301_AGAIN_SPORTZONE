package com.sportzone.util;

import com.sportzone.model.Account;
import javax.servlet.http.HttpSession;

public final class SessionUtil {

    private SessionUtil() {
    }

    public static void storeLoggedInAccount(HttpSession session, Account account) {
        // Xóa hash trước khi lưu: session có thể bị serialize ra disk hoặc dump log,
        // lộ hash là tấn công pass-the-hash được ngay dù mật khẩu gốc không lộ.
        account.setPasswordHash(null);
        session.setAttribute(AppConstants.SESSION_ACCOUNT, account);
        session.setAttribute(AppConstants.SESSION_ROLE, account.getRoleName());
        session.setAttribute(AppConstants.SESSION_FROZEN, account.isFrozen());
    }

    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(AppConstants.SESSION_ACCOUNT) != null;
    }
}
