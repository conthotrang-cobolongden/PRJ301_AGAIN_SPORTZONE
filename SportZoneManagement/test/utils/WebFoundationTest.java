package utils;

import model.Account;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Kiem tra contract CSRF va session cua Phase 1. */
public final class WebFoundationTest {

    private WebFoundationTest() { }

    public static void main(String[] args) {
        Map<String, Object> sessionData = new HashMap<>();
        HttpSession session = sessionProxy(sessionData);

        String token = CsrfUtil.ensureToken(session);
        require(token != null && token.length() >= 32, "CSRF token must be strong");
        require(token.equals(CsrfUtil.ensureToken(session)), "CSRF token must be stable in one session");
        require(CsrfUtil.isValid(requestProxy(session, token)), "Matching CSRF token must pass");
        require(!CsrfUtil.isValid(requestProxy(session, "invalid")), "Invalid CSRF token must fail");

        Account account = new Account();
        account.setFullName("SportZone User");
        account.setPasswordHash("secret-hash");
        account.setRoleName(AppConstants.ROLE_USER);
        account.setStatus(AppConstants.ACCOUNT_FROZEN);
        SessionUtil.storeLoggedInAccount(session, account);

        require(SessionUtil.isLoggedIn(session), "Stored account must be logged in");
        require(account.getPasswordHash() == null, "Password hash must never remain in session");
        require(Boolean.TRUE.equals(sessionData.get(AppConstants.SESSION_FROZEN)), "Frozen flag must be stored");
        require(AppConstants.ROLE_USER.equals(sessionData.get(AppConstants.SESSION_ROLE_NAME)), "Role must be stored");

        System.out.println("WebFoundationTest PASSED");
    }

    private static HttpSession sessionProxy(Map<String, Object> data) {
        return (HttpSession) Proxy.newProxyInstance(WebFoundationTest.class.getClassLoader(),
                new Class<?>[]{HttpSession.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getAttribute": return data.get((String) args[0]);
                        case "setAttribute": data.put((String) args[0], args[1]); return null;
                        case "removeAttribute": data.remove((String) args[0]); return null;
                        default: return defaultValue(method.getReturnType());
                    }
                });
    }

    private static HttpServletRequest requestProxy(HttpSession session, String csrfToken) {
        return (HttpServletRequest) Proxy.newProxyInstance(WebFoundationTest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class}, (proxy, method, args) -> {
                    if ("getSession".equals(method.getName())) return session;
                    if ("getParameter".equals(method.getName()) && "csrfToken".equals(args[0])) return csrfToken;
                    return defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
