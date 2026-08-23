package filter;

import controller.AuthServletTest;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public final class FrozenAccountFilterTest {

    private FrozenAccountFilterTest() {
    }

    public static void main(String[] args)
            throws Exception {
        guestCanContinue();
        activeAccountCanContinue();
        frozenCannotAccessHome();
        frozenCanOnlyAccessAllowedPaths();
        webContractIsConfigured();

        /*
         * Chay lai auth regression cu trong cung mot lenh.
         */
        AuthServletTest.main(new String[0]);

        System.out.println(
                "FrozenAccountFilterTest PASSED"
        );
    }

    private static void guestCanContinue()
            throws Exception {
        Result result = execute(
                "/MainController",
                "home",
                null
        );

        require(
                result.chained,
                "Guest public request phai tiep tuc"
        );
    }

    private static void activeAccountCanContinue()
            throws Exception {
        Result result = execute(
                "/MainController",
                "home",
                session("Active", false)
        );

        require(
                result.chained,
                "Active account phai truy cap duoc home"
        );
    }

    private static void frozenCannotAccessHome()
            throws Exception {
        Result result = execute(
                "/MainController",
                "home",
                session("Frozen", true)
        );

        require(
                !result.chained,
                "Frozen khong duoc vao home"
        );

        require(
                "/SportZone/MainController?action=accountLocked".equals(
                        result.redirect
                ),
                "Frozen phai bi redirect sang account-locked"
        );
    }

    private static void frozenCanOnlyAccessAllowedPaths()
            throws Exception {
        String[] allowedPaths = {"/assets/css/app.css"};

        for (String path : allowedPaths) {
            Result result = execute(
                    path,
                    session("Frozen", true)
            );

            require(
                    result.chained,
                    "Frozen phai truy cap duoc: " + path
            );

            require(
                    result.redirect == null,
                    "Allowed path khong duoc redirect: "
                    + path
            );
        }

        Result locked = execute(
                "/MainController",
                "accountLocked",
                session("Frozen", true)
        );

        require(
                locked.chained && locked.redirect == null,
                "Frozen phai xem accountLocked qua MainController"
        );

        Result logout = execute(
                "/MainController",
                "logout",
                session("Frozen", true)
        );

        require(
                logout.chained && logout.redirect == null,
                "Frozen phai logout duoc qua MainController"
        );

        String[] blockedPaths = {
            "/",
            "/home",
            "/MainController",
            "/courts",
            "/profile",
            "/account-locked",
            "/bookings",
            "/booking/create",
            "/invoices",
            "/admin"
        };

        for (String path : blockedPaths) {
            Result result = execute(
                    path,
                    session("Frozen", true)
            );

            require(
                    !result.chained,
                    "Frozen phai bi chan: " + path
            );

            require(
                    "/SportZone/MainController?action=accountLocked".equals(
                            result.redirect
                    ),
                    "Blocked path phai redirect account-locked: "
                    + path
            );
        }

        String[] blockedActions = {
            "home", "courts", "profile", "password",
            "bookingCreate", "bookings", "bookingDetail",
            "invoicePayment", "invoices", "invoiceDetail"
        };

        for (String action : blockedActions) {
            Result result = execute(
                    "/MainController",
                    action,
                    session("Frozen", true)
            );

            require(
                    !result.chained
                    && "/SportZone/MainController?action=accountLocked".equals(result.redirect),
                    "Frozen phai bi chan tai MainController action: " + action
            );
        }
    }

    private static void webContractIsConfigured()
            throws Exception {
        String webXml = readProjectFile(
                "web/WEB-INF/web.xml"
        );

        String loginServlet = readProjectFile(
                "src/java/controller/LoginServlet.java"
        );

        String lockedJsp = readProjectFile(
                "web/WEB-INF/views/error/account-locked.jsp"
        );

        require(
                webXml.contains(
                        "filter.FrozenAccountFilter"
                ),
                "web.xml phai khai bao FrozenAccountFilter"
        );

        require(
                webXml.contains(
                        "<url-pattern>/*</url-pattern>"
                ),
                "FrozenAccountFilter phai bao ve toan app"
        );

        require(
                webXml.contains(
                        "controller.AccountLockedServlet"
                )
                && !webXml.contains(
                        "<url-pattern>/account-locked</url-pattern>"
                ),
                "AccountLockedServlet phai la named-dispatch target, khong direct mapping"
        );

        require(
                loginServlet.contains(
                        "\"/MainController?action=accountLocked\""
                )
                && loginServlet.contains(
                        "SessionUtil.isFrozen"
                ),
                "Frozen login phai redirect account-locked"
        );

        require(
                lockedJsp.contains(
                        "Tài khoản đã bị khóa"
                ),
                "Locked JSP phai co thong bao ro rang"
        );

        require(
                lockedJsp.contains(
                        "(+84) 901 234 567"
                )
                && lockedJsp.contains(
                        "support@sportzone.vn"
                ),
                "Locked JSP phai co thong tin lien he"
        );
    }

    private static Result execute(
            String servletPath,
            HttpSession session
    ) throws Exception {
        return execute(servletPath, null, session);
    }

    private static Result execute(
            String servletPath,
            String action,
            HttpSession session
    ) throws Exception {
        AtomicBoolean chained
                = new AtomicBoolean(false);

        AtomicReference<String> redirect
                = new AtomicReference<>();

        HttpServletRequest request
                = proxy(
                        HttpServletRequest.class,
                        (proxy, method, arguments) -> {
                            switch (method.getName()) {
                                case "getSession":
                                    return session;

                                case "getServletPath":
                                    return servletPath;

                                case "getContextPath":
                                    return "/SportZone";

                                case "getParameter":
                                    return "action".equals(
                                            arguments[0]
                                    ) ? action : null;

                                default:
                                    return defaultValue(
                                            method.getReturnType()
                                    );
                            }
                        }
                );

        HttpServletResponse response
                = proxy(
                        HttpServletResponse.class,
                        (proxy, method, arguments) -> {
                            if ("sendRedirect".equals(
                                    method.getName()
                            )) {
                                redirect.set(
                                        (String) arguments[0]
                                );
                            }

                            return defaultValue(
                                    method.getReturnType()
                            );
                        }
                );

        FilterChain chain
                = (requestValue, responseValue)
                -> chained.set(true);

        new FrozenAccountFilter().doFilter(
                request,
                response,
                chain
        );

        return new Result(
                chained.get(),
                redirect.get()
        );
    }

    private static HttpSession session(
            String status,
            boolean frozen
    ) {
        Map<String, Object> attributes
                = new HashMap<>();

        Account account = new Account();
        account.setStatus(status);
        account.setRoleName(
                AppConstants.ROLE_USER
        );

        attributes.put(
                AppConstants.SESSION_ACCOUNT,
                account
        );

        attributes.put(
                AppConstants.SESSION_FROZEN,
                frozen
        );

        return proxy(
                HttpSession.class,
                (proxy, method, arguments) -> {
                    if ("getAttribute".equals(
                            method.getName()
                    )) {
                        return attributes.get(
                                (String) arguments[0]
                        );
                    }

                    return defaultValue(
                            method.getReturnType()
                    );
                }
        );
    }

    private static String readProjectFile(
            String relativePath
    ) throws Exception {
        Path current = Paths.get("")
                .toAbsolutePath();

        while (current != null) {
            Path direct = current.resolve(
                    relativePath
            );

            if (Files.exists(direct)) {
                return new String(
                        Files.readAllBytes(direct),
                        StandardCharsets.UTF_8
                );
            }

            Path nested = current
                    .resolve("SportZoneManagement")
                    .resolve(relativePath);

            if (Files.exists(nested)) {
                return new String(
                        Files.readAllBytes(nested),
                        StandardCharsets.UTF_8
                );
            }

            current = current.getParent();
        }

        throw new IllegalStateException(
                "Khong tim thay: " + relativePath
        );
    }

    private static <T> T proxy(
            Class<T> type,
            java.lang.reflect.InvocationHandler handler
    ) {
        return type.cast(
                Proxy.newProxyInstance(
                        FrozenAccountFilterTest.class
                                .getClassLoader(),
                        new Class<?>[]{type},
                        handler
                )
        );
    }

    private static Object defaultValue(
            Class<?> type
    ) {
        if (!type.isPrimitive()) {
            return null;
        }

        if (type == boolean.class) {
            return false;
        }

        if (type == byte.class) {
            return (byte) 0;
        }

        if (type == short.class) {
            return (short) 0;
        }

        if (type == int.class) {
            return 0;
        }

        if (type == long.class) {
            return 0L;
        }

        if (type == float.class) {
            return 0F;
        }

        if (type == double.class) {
            return 0D;
        }

        if (type == char.class) {
            return '\0';
        }

        return null;
    }

    private static void require(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class Result {

        private final boolean chained;
        private final String redirect;

        private Result(
                boolean chained,
                String redirect
        ) {
            this.chained = chained;
            this.redirect = redirect;
        }
    }
}
