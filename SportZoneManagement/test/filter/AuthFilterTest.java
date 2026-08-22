package filter;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import utils.AppConstants;

/** Kiem tra AuthFilter bao ve request va ghi nho dich GET an toan. */
public final class AuthFilterTest {
    private AuthFilterTest() { }

    public static void main(String[] args) throws Exception {
        loggedInRequestContinuesChain();
        anonymousGetSavesTargetAndRedirects();
        anonymousPostDoesNotSaveTargetAndRedirects();
        System.out.println("AuthFilterTest PASSED");
    }

    private static void loggedInRequestContinuesChain() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AppConstants.SESSION_ACCOUNT, new Account());
        HttpSession session = session(attributes);
        HttpServletRequest request = request("GET", "/booking", null, "/SportZone", session);
        AtomicBoolean chained = new AtomicBoolean();
        AtomicReference<String> redirect = new AtomicReference<>();

        new AuthFilter().doFilter(request, response(redirect), chain(chained));

        require(chained.get(), "Logged-in request must continue the chain");
        require(redirect.get() == null, "Logged-in request must not redirect");
    }

    private static void anonymousGetSavesTargetAndRedirects() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession createdSession = session(attributes);
        HttpServletRequest request = request("GET", "/booking", "id=17",
                "/SportZone", null, createdSession);
        AtomicBoolean chained = new AtomicBoolean();
        AtomicReference<String> redirect = new AtomicReference<>();

        new AuthFilter().doFilter(request, response(redirect), chain(chained));

        require(!chained.get(), "Anonymous GET must not continue the chain");
        require("/booking?id=17".equals(attributes.get(AppConstants.SESSION_REDIRECT_AFTER_LOGIN)),
                "Anonymous GET must save servlet path and query");
        require("/SportZone/login".equals(redirect.get()),
                "Anonymous GET must redirect to context login URL");
    }

    private static void anonymousPostDoesNotSaveTargetAndRedirects() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession createdSession = session(attributes);
        HttpServletRequest request = request("POST", "/booking", "id=17",
                "/SportZone", null, createdSession);
        AtomicBoolean chained = new AtomicBoolean();
        AtomicReference<String> redirect = new AtomicReference<>();

        new AuthFilter().doFilter(request, response(redirect), chain(chained));

        require(!chained.get(), "Anonymous POST must not continue the chain");
        require(!attributes.containsKey(AppConstants.SESSION_REDIRECT_AFTER_LOGIN),
                "Anonymous POST must not save a replay target");
        require("/SportZone/login".equals(redirect.get()),
                "Anonymous POST must redirect to context login URL");
    }

    private static FilterChain chain(AtomicBoolean chained) {
        return (request, response) -> chained.set(true);
    }

    private static HttpServletResponse response(AtomicReference<String> redirect) {
        return proxy(HttpServletResponse.class, (proxy, method, args) -> {
            if ("sendRedirect".equals(method.getName())) {
                redirect.set((String) args[0]);
            }
            return defaultValue(method.getReturnType());
        });
    }

    private static HttpSession session(Map<String, Object> attributes) {
        return proxy(HttpSession.class, (proxy, method, args) -> {
            if ("getAttribute".equals(method.getName())) {
                return attributes.get((String) args[0]);
            }
            if ("setAttribute".equals(method.getName())) {
                attributes.put((String) args[0], args[1]);
            }
            return defaultValue(method.getReturnType());
        });
    }

    private static HttpServletRequest request(String httpMethod, String servletPath,
            String query, String contextPath, HttpSession existingSession) {
        return request(httpMethod, servletPath, query, contextPath, existingSession, existingSession);
    }

    private static HttpServletRequest request(String httpMethod, String servletPath,
            String query, String contextPath, HttpSession existingSession, HttpSession createdSession) {
        return proxy(HttpServletRequest.class, (proxy, method, args) -> {
            switch (method.getName()) {
                case "getMethod": return httpMethod;
                case "getServletPath": return servletPath;
                case "getQueryString": return query;
                case "getContextPath": return contextPath;
                case "getSession":
                    return args == null || args.length == 0 ? createdSession : existingSession;
                default: return defaultValue(method.getReturnType());
            }
        });
    }

    private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(AuthFilterTest.class.getClassLoader(),
                new Class<?>[]{type}, handler));
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        return null;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
