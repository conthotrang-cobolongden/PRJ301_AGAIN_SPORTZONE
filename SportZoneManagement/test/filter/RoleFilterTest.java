package filter;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.AppConstants;

/** Kiem tra RoleFilter chi cho phep tai khoan Admin di tiep. */
public final class RoleFilterTest {
    private RoleFilterTest() { }

    public static void main(String[] args) throws Exception {
        adminContinuesChain();
        userReceivesForbidden();
        missingSessionReceivesForbidden();
        System.out.println("RoleFilterTest PASSED");
    }

    private static void adminContinuesChain() throws Exception {
        HttpSession session = sessionWithRole(AppConstants.ROLE_ADMIN);
        AtomicBoolean chained = new AtomicBoolean();
        AtomicInteger error = new AtomicInteger();

        new RoleFilter().doFilter(request(session), response(error), chain(chained));

        require(chained.get(), "Admin request must continue the chain");
        require(error.get() == 0, "Admin request must not receive an error");
    }

    private static void userReceivesForbidden() throws Exception {
        HttpSession session = sessionWithRole(AppConstants.ROLE_USER);
        AtomicBoolean chained = new AtomicBoolean();
        AtomicInteger error = new AtomicInteger();

        new RoleFilter().doFilter(request(session), response(error), chain(chained));

        require(!chained.get(), "User request must not continue the chain");
        require(error.get() == HttpServletResponse.SC_FORBIDDEN,
                "User request must receive HTTP 403");
    }

    private static void missingSessionReceivesForbidden() throws Exception {
        AtomicBoolean chained = new AtomicBoolean();
        AtomicInteger error = new AtomicInteger();

        new RoleFilter().doFilter(request(null), response(error), chain(chained));

        require(!chained.get(), "Request without a session must not continue the chain");
        require(error.get() == HttpServletResponse.SC_FORBIDDEN,
                "Request without a session must receive HTTP 403");
    }

    private static FilterChain chain(AtomicBoolean chained) {
        return (request, response) -> chained.set(true);
    }

    private static HttpServletRequest request(HttpSession session) {
        return proxy(HttpServletRequest.class, (proxy, method, args) -> {
            if ("getSession".equals(method.getName())) return session;
            return defaultValue(method.getReturnType());
        });
    }

    private static HttpServletResponse response(AtomicInteger error) {
        return proxy(HttpServletResponse.class, (proxy, method, args) -> {
            if ("sendError".equals(method.getName())) error.set((Integer) args[0]);
            return defaultValue(method.getReturnType());
        });
    }

    private static HttpSession sessionWithRole(String roleName) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AppConstants.SESSION_ROLE_NAME, roleName);
        return proxy(HttpSession.class, (proxy, method, args) -> {
            if ("getAttribute".equals(method.getName())) {
                return attributes.get((String) args[0]);
            }
            return defaultValue(method.getReturnType());
        });
    }

    private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(RoleFilterTest.class.getClassLoader(),
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
