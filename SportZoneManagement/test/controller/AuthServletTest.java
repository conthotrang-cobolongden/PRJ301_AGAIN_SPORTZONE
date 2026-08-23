package controller;

import dao.AccountDAO;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import service.AuthService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.Views;

/**
 * Main-method tests for the login HTTP flow using only servlet API proxies.
 */
public final class AuthServletTest {

    private AuthServletTest() {
    }

    public static void main(String[] args) throws Exception {
        testLoginGetCreatesCsrfAndForwardsToViewsLogin();
        testLoggedInLoginGetRedirectsHome();
        testLoginPostRejectsInvalidCsrfWith403();
        testLoginFailureUsesGenericMessageAndKeepsEmailOnly();
        testLoginSuccessStoresSafeSessionAndConsumesSavedTarget();
        testLoginSuccessDefaultsToHomeForAdminAndUser();
        testRegisterGetCreatesCsrfAndForwards();
        testRegisterPostRejectsInvalidCsrfWithoutCallingService();
        testRegisterValidationPreservesOnlyNameEmailPhone();
        testRegisterUniqueViolationBecomesFriendlyFieldError();
        testRegisterSuccessLogsInAndRedirectsHome();
        testLogoutInvalidatesWholeSessionAndRedirectsHome();
        System.out.println("AuthServletTest PASSED");
    }

    private static void testLoginGetCreatesCsrfAndForwardsToViewsLogin()
            throws Exception {
        Fixture fixture = new Fixture("/SportZone");

        new TestLoginServlet(new FakeAuthService()).get(fixture.request, fixture.response);

        Object csrfToken = fixture.sessionData.get(AppConstants.SESSION_CSRF_TOKEN);
        require(csrfToken instanceof String && !((String) csrfToken).isEmpty(),
                "GET /login must create a CSRF token");
        require(Views.LOGIN.equals(fixture.forwardedPath),
                "GET /login must forward to Views.LOGIN");
        require(fixture.redirectLocation == null,
                "Logged-out GET /login must not redirect");
    }

    private static void testLoggedInLoginGetRedirectsHome() throws Exception {
        Fixture fixture = new Fixture("/SportZone");
        fixture.sessionData.put(AppConstants.SESSION_ACCOUNT, account(
                AppConstants.ROLE_USER, AppConstants.ACCOUNT_ACTIVE));

        new TestLoginServlet(new FakeAuthService()).get(fixture.request, fixture.response);

        require("/SportZone/home".equals(fixture.redirectLocation),
                "Logged-in GET /login must redirect to context /home");
        require(fixture.forwardedPath == null,
                "Logged-in GET /login must not forward to the login view");
    }

    private static void testLoginPostRejectsInvalidCsrfWith403() throws Exception {
        Fixture fixture = new Fixture("/SportZone");
        fixture.sessionData.put(AppConstants.SESSION_CSRF_TOKEN, "expected-token");
        fixture.parameters.put(CsrfUtil.PARAMETER_NAME, "wrong-token");
        fixture.parameters.put("email", "member@sportzone.local");
        fixture.parameters.put("password", "Secret@123");
        FakeAuthService authService = new FakeAuthService();
        authService.result = account(AppConstants.ROLE_USER, AppConstants.ACCOUNT_ACTIVE);

        new TestLoginServlet(authService).post(fixture.request, fixture.response);

        require(fixture.errorStatus == HttpServletResponse.SC_FORBIDDEN,
                "Invalid CSRF must return HTTP 403");
        require(authService.calls == 0,
                "Invalid CSRF must return before authentication");
        require(!fixture.requestedParameters.contains("email")
                && !fixture.requestedParameters.contains("password"),
                "Invalid CSRF must return before reading credentials");
        require(!fixture.sessionData.containsKey(AppConstants.SESSION_ACCOUNT),
                "Invalid CSRF must not authenticate the request");
    }

    private static void testLoginFailureUsesGenericMessageAndKeepsEmailOnly()
            throws Exception {
        Fixture fixture = validPostFixture("/SportZone");
        fixture.parameters.put("email", "  USER@SPORTZONE.LOCAL  ");
        fixture.parameters.put("password", " raw password ");
        FakeAuthService authService = new FakeAuthService();

        new TestLoginServlet(authService).post(fixture.request, fixture.response);

        require(authService.calls == 1,
                "Valid login submission must call AuthService once");
        require("USER@SPORTZONE.LOCAL".equals(authService.email),
                "Servlet must pass a trimmed email to AuthService");
        require(" raw password ".equals(authService.password),
                "Servlet must preserve the raw password for AuthService");
        require("Email hoặc mật khẩu không đúng.".equals(
                fixture.requestAttributes.get(AppConstants.ATTRIBUTE_ERROR_MESSAGE)),
                "Failed login must use the exact generic message");
        require("USER@SPORTZONE.LOCAL".equals(fixture.requestAttributes.get("email")),
                "Failed login must retain only the trimmed email");
        require(!fixture.requestAttributes.containsKey("password"),
                "Failed login must never retain the password");
        require(Views.LOGIN.equals(fixture.forwardedPath),
                "Failed login must forward back to Views.LOGIN");
        require(fixture.sessionData.get(AppConstants.SESSION_CSRF_TOKEN) instanceof String,
                "Failed login must leave a CSRF token available for retry");
    }

    private static void testLoginSuccessStoresSafeSessionAndConsumesSavedTarget()
            throws Exception {
        Fixture fixture = validPostFixture("/SportZone");
        fixture.parameters.put("email", "user@sportzone.local");
        fixture.parameters.put("password", "Secret@123");
        fixture.sessionData.put(AppConstants.SESSION_REDIRECT_AFTER_LOGIN,
                "/profile?tab=security");
        Account account = account(AppConstants.ROLE_USER, AppConstants.ACCOUNT_FROZEN);
        account.setPasswordHash("must-not-enter-session");
        FakeAuthService authService = new FakeAuthService();
        authService.result = account;

        new TestLoginServlet(authService).post(fixture.request, fixture.response);

        require(fixture.sessionData.get(AppConstants.SESSION_ACCOUNT) == account,
                "Successful login must store the Account in session");
        require(account.getPasswordHash() == null,
                "Successful login must clear the Account password hash");
        require(AppConstants.ROLE_USER.equals(
                fixture.sessionData.get(AppConstants.SESSION_ROLE_NAME)),
                "Successful login must store roleName through SessionUtil");
        require(Boolean.TRUE.equals(fixture.sessionData.get(AppConstants.SESSION_FROZEN)),
                "Successful login must store frozen state through SessionUtil");
        require(!fixture.sessionData.containsKey(AppConstants.SESSION_REDIRECT_AFTER_LOGIN),
                "Successful login must consume redirectAfterLogin exactly once");
        require(
                "/SportZone/account-locked".equals(
                        fixture.redirectLocation
                ),
                "Frozen login must ignore saved target and redirect to locked page"
        );
    }

    private static void testLoginSuccessDefaultsToHomeForAdminAndUser()
            throws Exception {
        String[] roles = {AppConstants.ROLE_ADMIN, AppConstants.ROLE_USER};
        for (String role : roles) {
            Fixture fixture = validPostFixture("/SportZone");
            fixture.parameters.put("email", role + "@sportzone.local");
            fixture.parameters.put("password", "Secret@123");
            FakeAuthService authService = new FakeAuthService();
            authService.result = account(role, AppConstants.ACCOUNT_ACTIVE);

            new TestLoginServlet(authService).post(fixture.request, fixture.response);

            require("/SportZone/home".equals(fixture.redirectLocation),
                    "Successful " + role + " login must default to context /home");
        }
    }

    private static void testRegisterGetCreatesCsrfAndForwards() throws Exception {
        Fixture fixture = new Fixture("/SportZone");

        new TestRegisterServlet(new FakeAuthService()).get(
                fixture.request, fixture.response);

        Object csrfToken = fixture.sessionData.get(AppConstants.SESSION_CSRF_TOKEN);
        require(csrfToken instanceof String && !((String) csrfToken).isEmpty(),
                "GET /register must create a CSRF token");
        require(Views.REGISTER.equals(fixture.forwardedPath),
                "GET /register must forward to Views.REGISTER");
        require(fixture.redirectLocation == null,
                "Logged-out GET /register must not redirect");

        Fixture loggedIn = new Fixture("/SportZone");
        loggedIn.sessionData.put(AppConstants.SESSION_ACCOUNT,
                account(AppConstants.ROLE_USER, AppConstants.ACCOUNT_ACTIVE));

        new TestRegisterServlet(new FakeAuthService()).get(
                loggedIn.request, loggedIn.response);

        require("/SportZone/home".equals(loggedIn.redirectLocation),
                "Logged-in GET /register must redirect to context /home");
        require(loggedIn.forwardedPath == null,
                "Logged-in GET /register must not forward to the register view");
    }

    private static void testRegisterPostRejectsInvalidCsrfWithoutCallingService()
            throws Exception {
        Fixture fixture = new Fixture("/SportZone");
        fixture.sessionData.put(AppConstants.SESSION_CSRF_TOKEN, "expected-token");
        fixture.parameters.put(CsrfUtil.PARAMETER_NAME, "wrong-token");
        fixture.parameters.put("fullName", "Member");
        fixture.parameters.put("email", "member@sportzone.local");
        fixture.parameters.put("phone", "0901234567");
        fixture.parameters.put("password", "Strong@123");
        fixture.parameters.put("confirmPassword", "Strong@123");
        FakeAuthService authService = new FakeAuthService();

        new TestRegisterServlet(authService).post(fixture.request, fixture.response);

        require(fixture.errorStatus == HttpServletResponse.SC_FORBIDDEN,
                "Invalid register CSRF must return HTTP 403");
        require(authService.registerCalls == 0,
                "Invalid register CSRF must not call AuthService.register");
        require(authService.registerDao.calls == 0,
                "Invalid register CSRF must not call the DAO");
        require(!fixture.requestedParameters.contains("fullName")
                && !fixture.requestedParameters.contains("email")
                && !fixture.requestedParameters.contains("phone")
                && !fixture.requestedParameters.contains("password")
                && !fixture.requestedParameters.contains("confirmPassword"),
                "Invalid register CSRF must return before reading form fields");
    }

    private static void testRegisterValidationPreservesOnlyNameEmailPhone()
            throws Exception {
        Fixture fixture = validPostFixture("/SportZone");
        fixture.parameters.put("fullName", "  ");
        fixture.parameters.put("email", "not-an-email");
        fixture.parameters.put("phone", "0123");
        fixture.parameters.put("password", "short-secret");
        fixture.parameters.put("confirmPassword", "different-secret");
        FakeRegistrationDAO dao = new FakeRegistrationDAO();

        new TestRegisterServlet(new AuthService(dao)).post(
                fixture.request, fixture.response);

        require(Views.REGISTER.equals(fixture.forwardedPath),
                "Invalid registration must forward back to Views.REGISTER");
        Object errors = fixture.requestAttributes.get(
                AppConstants.ATTRIBUTE_FIELD_ERRORS);
        require(errors instanceof Map && !((Map<?, ?>) errors).isEmpty(),
                "Invalid registration must expose field errors");
        require("  ".equals(fixture.requestAttributes.get("fullName")),
                "Invalid registration must preserve fullName");
        require("not-an-email".equals(fixture.requestAttributes.get("email")),
                "Invalid registration must preserve email");
        require("0123".equals(fixture.requestAttributes.get("phone")),
                "Invalid registration must preserve phone");
        require(!fixture.requestAttributes.containsKey("password")
                && !fixture.requestAttributes.containsKey("confirmPassword"),
                "Registration errors must never retain either password field");
        require(dao.calls == 0,
                "Syntactically invalid registration must not call the DAO");
        require(fixture.sessionData.get(AppConstants.SESSION_CSRF_TOKEN) instanceof String,
                "Invalid registration must leave a CSRF token available for retry");
    }

    private static void testRegisterUniqueViolationBecomesFriendlyFieldError()
            throws Exception {
        Fixture fixture = validRegisterFixture("/SportZone");
        FakeRegistrationDAO dao = new FakeRegistrationDAO();
        dao.insertFailure = new SQLException("duplicate", "23000");

        new TestRegisterServlet(new AuthService(dao)).post(
                fixture.request, fixture.response);

        Object rawErrors = fixture.requestAttributes.get(
                AppConstants.ATTRIBUTE_FIELD_ERRORS);
        require(rawErrors instanceof Map,
                "Duplicate insert must render field errors");
        Map<?, ?> errors = (Map<?, ?>) rawErrors;
        require("Email hoặc số điện thoại đã được sử dụng".equals(errors.get("email")),
                "SQLState 23000 must become the friendly duplicate field error");
        require(errors.size() == 1 && new ArrayList<>(errors.keySet()).get(0).equals("email"),
                "Duplicate insert error must be an ordered email field error");
        require(Views.REGISTER.equals(fixture.forwardedPath),
                "Duplicate insert must return to Views.REGISTER");
        require(!fixture.requestAttributes.containsKey("password")
                && !fixture.requestAttributes.containsKey("confirmPassword"),
                "Duplicate errors must never retain either password field");
        require(dao.insertCalls == 1,
                "Duplicate race must be raised by the insert path");
    }

    private static void testRegisterSuccessLogsInAndRedirectsHome()
            throws Exception {
        Fixture fixture = validRegisterFixture("/SportZone");
        FakeRegistrationDAO dao = new FakeRegistrationDAO();
        Account created = account(AppConstants.ROLE_USER, AppConstants.ACCOUNT_ACTIVE);
        created.setPasswordHash("must-not-enter-session");
        dao.idResult = created;

        new TestRegisterServlet(new AuthService(dao)).post(
                fixture.request, fixture.response);

        require(fixture.sessionData.get(AppConstants.SESSION_ACCOUNT) == created,
                "Successful registration must log in the created account");
        require(created.getPasswordHash() == null,
                "Successful registration must clear the session account password hash");
        require(AppConstants.ROLE_USER.equals(
                fixture.sessionData.get(AppConstants.SESSION_ROLE_NAME)),
                "Successful registration must use SessionUtil role state");
        require(Boolean.FALSE.equals(fixture.sessionData.get(AppConstants.SESSION_FROZEN)),
                "Successful registration must use SessionUtil frozen state");
        require("/SportZone/home".equals(fixture.redirectLocation),
                "Successful registration must redirect to context /home");
    }

    private static void testLogoutInvalidatesWholeSessionAndRedirectsHome()
            throws Exception {
        Fixture fixture = new Fixture("/SportZone");
        fixture.sessionData.put(AppConstants.SESSION_ACCOUNT,
                account(AppConstants.ROLE_USER, AppConstants.ACCOUNT_ACTIVE));
        fixture.sessionData.put("unrelatedSessionState", "must-also-be-removed");

        new TestLogoutServlet().get(fixture.request, fixture.response);

        require(fixture.sessionInvalidated,
                "Logout must invalidate the entire existing session");
        require(fixture.sessionData.isEmpty(),
                "Session invalidation must remove all session state");
        require("/SportZone/home".equals(fixture.redirectLocation),
                "Logout must redirect to context /home");
    }

    private static Fixture validPostFixture(String contextPath) {
        Fixture fixture = new Fixture(contextPath);
        fixture.sessionData.put(AppConstants.SESSION_CSRF_TOKEN, "valid-token");
        fixture.parameters.put(CsrfUtil.PARAMETER_NAME, "valid-token");
        return fixture;
    }

    private static Fixture validRegisterFixture(String contextPath) {
        Fixture fixture = validPostFixture(contextPath);
        fixture.parameters.put("fullName", "New Member");
        fixture.parameters.put("email", "new.member@sportzone.local");
        fixture.parameters.put("phone", "0901234567");
        fixture.parameters.put("password", "Strong@123");
        fixture.parameters.put("confirmPassword", "Strong@123");
        return fixture;
    }

    private static Account account(String roleName, String status) {
        Account account = new Account();
        account.setEmail(roleName.toLowerCase() + "@sportzone.local");
        account.setRoleName(roleName);
        account.setStatus(status);
        return account;
    }

    private static Object defaultValue(Class<?> type) {
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
            return (char) 0;
        }
        return null;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class TestLoginServlet extends LoginServlet {

        TestLoginServlet(AuthService authService) {
            super(authService);
        }

        void get(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            super.doGet(request, response);
        }

        void post(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            super.doPost(request, response);
        }
    }

    private static final class TestRegisterServlet extends RegisterServlet {

        TestRegisterServlet(AuthService authService) {
            super(authService);
        }

        void get(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            super.doGet(request, response);
        }

        void post(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            super.doPost(request, response);
        }
    }

    private static final class TestLogoutServlet extends LogoutServlet {

        void get(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            super.doGet(request, response);
        }
    }

    private static final class FakeAuthService extends AuthService {

        private final FakeRegistrationDAO registerDao;
        private Account result;
        private int calls;
        private int registerCalls;
        private String email;
        private String password;

        FakeAuthService() {
            this(new FakeRegistrationDAO());
        }

        private FakeAuthService(FakeRegistrationDAO registerDao) {
            super(registerDao);
            this.registerDao = registerDao;
        }

        @Override
        public Account login(String email, String password) throws SQLException {
            calls++;
            this.email = email;
            this.password = password;
            return result;
        }

        @Override
        public RegisterResult register(String fullName, String email, String phone,
                String password, String confirmPassword) throws SQLException {
            registerCalls++;
            return null;
        }
    }

    private static final class FakeRegistrationDAO extends AccountDAO {

        private int calls;
        private int insertCalls;
        private Account idResult;
        private SQLException insertFailure;

        @Override
        public boolean existsByEmail(String email) {
            calls++;
            return false;
        }

        @Override
        public boolean existsByPhone(String phone) {
            calls++;
            return false;
        }

        @Override
        public int insert(Account account) throws SQLException {
            calls++;
            insertCalls++;
            if (insertFailure != null) {
                throw insertFailure;
            }
            return 101;
        }

        @Override
        public Account findById(int accountID) {
            calls++;
            return idResult;
        }
    }

    private static final class Fixture {

        private final Map<String, Object> sessionData = new HashMap<>();
        private final Map<String, Object> requestAttributes = new HashMap<>();
        private final Map<String, String> parameters = new HashMap<>();
        private final List<String> requestedParameters = new ArrayList<>();
        private final HttpSession session;
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private String forwardedPath;
        private String redirectLocation;
        private int errorStatus = -1;
        private boolean sessionInvalidated;

        Fixture(String contextPath) {
            session = (HttpSession) Proxy.newProxyInstance(
                    AuthServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpSession.class}, (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "getAttribute":
                                return sessionData.get((String) args[0]);
                            case "setAttribute":
                                sessionData.put((String) args[0], args[1]);
                                return null;
                            case "removeAttribute":
                                sessionData.remove((String) args[0]);
                                return null;
                            case "invalidate":
                                sessionInvalidated = true;
                                sessionData.clear();
                                return null;
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    });

            request = (HttpServletRequest) Proxy.newProxyInstance(
                    AuthServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class}, (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "getSession":
                                return session;
                            case "getParameter":
                                String name = (String) args[0];
                                requestedParameters.add(name);
                                return parameters.get(name);
                            case "getContextPath":
                                return contextPath;
                            case "setAttribute":
                                requestAttributes.put((String) args[0], args[1]);
                                return null;
                            case "getAttribute":
                                return requestAttributes.get((String) args[0]);
                            case "getRequestDispatcher":
                                String path = (String) args[0];
                                return dispatcher(path);
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    });

            response = (HttpServletResponse) Proxy.newProxyInstance(
                    AuthServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class}, (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "sendRedirect":
                                redirectLocation = (String) args[0];
                                return null;
                            case "sendError":
                                errorStatus = (Integer) args[0];
                                return null;
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    });
        }

        private RequestDispatcher dispatcher(String path) {
            return (RequestDispatcher) Proxy.newProxyInstance(
                    AuthServletTest.class.getClassLoader(),
                    new Class<?>[]{RequestDispatcher.class}, (proxy, method, args) -> {
                        if ("forward".equals(method.getName())) {
                            forwardedPath = path;
                        }
                        return defaultValue(method.getReturnType());
                    });
        }
    }
}
