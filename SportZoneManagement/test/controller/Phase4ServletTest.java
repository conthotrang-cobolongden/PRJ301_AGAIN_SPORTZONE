package controller;

import dao.AccountDAO;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import service.ProfileService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.PasswordUtil;
import utils.Views;

/** Servlet-level contract tests for the shared Admin/User profile screens. */
public final class Phase4ServletTest {

    private Phase4ServletTest() {
    }

    public static void main(String[] args) throws Exception {
        testProfileGetForwardsCurrentAccountAndSuccessMessage();
        testProfilePostRejectsInvalidCsrfBeforeReadingFields();
        testProfilePostValidationPreservesSubmittedProfile();
        testProfilePostUpdatesSessionAndRedirects();
        testPasswordGetForwardsSuccessMessage();
        testPasswordPostRejectsInvalidCsrfBeforeReadingFields();
        testPasswordPostRejectsWrongCurrentPasswordWithoutRetainingSecrets();
        testPasswordPostUpdatesHashKeepsSessionAndRedirects();
        System.out.println("Phase4ServletTest PASSED");
    }

    private static void testProfileGetForwardsCurrentAccountAndSuccessMessage()
            throws Exception {
        Fixture fixture = loggedInFixture();
        fixture.parameters.put("updated", "1");

        new TestProfileServlet(new ProfileService(new FakeAccountDAO()))
                .get(fixture.request, fixture.response);

        require(Views.PROFILE.equals(fixture.forwardedPath),
                "GET /profile must forward to Views.PROFILE");
        require(fixture.requestAttributes.get("account")
                == fixture.sessionData.get(AppConstants.SESSION_ACCOUNT),
                "GET /profile must expose the current session account");
        require(fixture.requestAttributes.get("success") instanceof String,
                "GET /profile?updated=1 must expose a success message");
        require("01/01/2026".equals(
                fixture.requestAttributes.get("memberSince")),
                "GET /profile must format LocalDateTime for JSTL-safe output");
        require(fixture.sessionData.get(AppConstants.SESSION_CSRF_TOKEN)
                instanceof String,
                "GET /profile must ensure a CSRF token");
    }

    private static void testProfilePostRejectsInvalidCsrfBeforeReadingFields()
            throws Exception {
        Fixture fixture = loggedInFixture();
        fixture.sessionData.put(AppConstants.SESSION_CSRF_TOKEN, "expected");
        fixture.parameters.put(CsrfUtil.PARAMETER_NAME, "wrong");
        fixture.parameters.put("fullName", "Changed");
        FakeAccountDAO dao = new FakeAccountDAO();

        new TestProfileServlet(new ProfileService(dao))
                .post(fixture.request, fixture.response);

        require(fixture.errorStatus == HttpServletResponse.SC_FORBIDDEN,
                "Invalid profile CSRF must return HTTP 403");
        require(fixture.requestedParameters.size() == 1
                && CsrfUtil.PARAMETER_NAME.equals(
                        fixture.requestedParameters.get(0)),
                "Invalid profile CSRF must be checked before form fields");
        require(dao.totalWrites() == 0,
                "Invalid profile CSRF must not write to DB");
    }

    private static void testProfilePostValidationPreservesSubmittedProfile()
            throws Exception {
        Fixture fixture = validPostFixture();
        fixture.parameters.put("fullName", " ");
        fixture.parameters.put("email", "invalid-email");
        fixture.parameters.put("phone", "0123");

        new TestProfileServlet(new ProfileService(new FakeAccountDAO()))
                .post(fixture.request, fixture.response);

        require(Views.PROFILE.equals(fixture.forwardedPath),
                "Invalid profile must return to Views.PROFILE");
        require(fixture.requestAttributes.get(
                AppConstants.ATTRIBUTE_FIELD_ERRORS) instanceof Map,
                "Invalid profile must expose field errors");
        Account formAccount = (Account) fixture.requestAttributes.get("account");
        require(formAccount != null,
                "Invalid profile must expose a form account");
        require(" ".equals(formAccount.getFullName())
                && "invalid-email".equals(formAccount.getEmail())
                && "0123".equals(formAccount.getPhone()),
                "Invalid profile must preserve submitted non-secret fields");
    }

    private static void testProfilePostUpdatesSessionAndRedirects()
            throws Exception {
        Fixture fixture = validPostFixture();
        fixture.parameters.put("fullName", "Updated Member");
        fixture.parameters.put("email", "updated@sportzone.local");
        fixture.parameters.put("phone", "0901234567");
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.idResult = account(7, "Updated Member",
                "updated@sportzone.local", "0901234567", "Active");
        dao.idResult.setPasswordHash("must-not-enter-session");

        new TestProfileServlet(new ProfileService(dao))
                .post(fixture.request, fixture.response);

        Account sessionAccount = (Account) fixture.sessionData.get(
                AppConstants.SESSION_ACCOUNT);
        require(sessionAccount == dao.idResult,
                "Successful profile update must refresh the session account");
        require(sessionAccount.getPasswordHash() == null,
                "Refreshed session account must not retain password hash");
        require("User".equals(fixture.sessionData.get(
                AppConstants.SESSION_ROLE_NAME)),
                "Profile refresh must preserve role session state");
        require(Boolean.FALSE.equals(fixture.sessionData.get(
                AppConstants.SESSION_FROZEN)),
                "Profile refresh must preserve frozen session state");
        require("/SportZone/profile?updated=1".equals(
                fixture.redirectLocation),
                "Successful profile update must use PRG redirect");
    }

    private static void testPasswordGetForwardsSuccessMessage()
            throws Exception {
        Fixture fixture = loggedInFixture();
        fixture.parameters.put("changed", "1");

        new TestPasswordServlet(new ProfileService(new FakeAccountDAO()))
                .get(fixture.request, fixture.response);

        require(Views.PASSWORD.equals(fixture.forwardedPath),
                "GET /password must forward to Views.PASSWORD");
        require(fixture.requestAttributes.get("success") instanceof String,
                "GET /password?changed=1 must expose a success message");
        require(fixture.sessionData.get(AppConstants.SESSION_CSRF_TOKEN)
                instanceof String,
                "GET /password must ensure a CSRF token");
    }

    private static void testPasswordPostRejectsInvalidCsrfBeforeReadingFields()
            throws Exception {
        Fixture fixture = loggedInFixture();
        fixture.sessionData.put(AppConstants.SESSION_CSRF_TOKEN, "expected");
        fixture.parameters.put(CsrfUtil.PARAMETER_NAME, "wrong");
        fixture.parameters.put("currentPassword", "Current@123");
        FakeAccountDAO dao = new FakeAccountDAO();

        new TestPasswordServlet(new ProfileService(dao))
                .post(fixture.request, fixture.response);

        require(fixture.errorStatus == HttpServletResponse.SC_FORBIDDEN,
                "Invalid password CSRF must return HTTP 403");
        require(fixture.requestedParameters.size() == 1
                && CsrfUtil.PARAMETER_NAME.equals(
                        fixture.requestedParameters.get(0)),
                "Invalid password CSRF must be checked before secrets");
        require(dao.totalWrites() == 0,
                "Invalid password CSRF must not write to DB");
    }

    private static void testPasswordPostRejectsWrongCurrentPasswordWithoutRetainingSecrets()
            throws Exception {
        Fixture fixture = validPostFixture();
        fixture.parameters.put("currentPassword", "Wrong@123");
        fixture.parameters.put("newPassword", "NewPassword@123");
        fixture.parameters.put("confirmPassword", "NewPassword@123");
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.idResult = account(7, "Member", "member@sportzone.local",
                "0901234567", "Active");
        dao.idResult.setPasswordHash(PasswordUtil.sha256Hex("Current@123"));

        new TestPasswordServlet(new ProfileService(dao))
                .post(fixture.request, fixture.response);

        require(Views.PASSWORD.equals(fixture.forwardedPath),
                "Wrong current password must return to Views.PASSWORD");
        Map<?, ?> errors = (Map<?, ?>) fixture.requestAttributes.get(
                AppConstants.ATTRIBUTE_FIELD_ERRORS);
        require(errors != null && errors.containsKey("currentPassword"),
                "Wrong current password must expose its field error");
        require(!fixture.requestAttributes.containsKey("currentPassword")
                && !fixture.requestAttributes.containsKey("newPassword")
                && !fixture.requestAttributes.containsKey("confirmPassword"),
                "Password errors must never retain submitted secrets");
        require(dao.passwordWrites == 0,
                "Wrong current password must not update DB");
    }

    private static void testPasswordPostUpdatesHashKeepsSessionAndRedirects()
            throws Exception {
        Fixture fixture = validPostFixture();
        fixture.parameters.put("currentPassword", "Current@123");
        fixture.parameters.put("newPassword", "NewPassword@123");
        fixture.parameters.put("confirmPassword", "NewPassword@123");
        FakeAccountDAO dao = new FakeAccountDAO();
        dao.idResult = account(7, "Member", "member@sportzone.local",
                "0901234567", "Active");
        dao.idResult.setPasswordHash(PasswordUtil.sha256Hex("Current@123"));
        Object originalSessionAccount = fixture.sessionData.get(
                AppConstants.SESSION_ACCOUNT);

        new TestPasswordServlet(new ProfileService(dao))
                .post(fixture.request, fixture.response);

        require(dao.passwordWrites == 1
                && PasswordUtil.matches("NewPassword@123", dao.passwordHash),
                "Successful password change must persist the new hash");
        require(fixture.sessionData.get(AppConstants.SESSION_ACCOUNT)
                == originalSessionAccount,
                "Password change must keep the current login session");
        require("/SportZone/password?changed=1".equals(
                fixture.redirectLocation),
                "Successful password change must use PRG redirect");
    }

    private static Fixture loggedInFixture() {
        Fixture fixture = new Fixture("/SportZone");
        fixture.sessionData.put(AppConstants.SESSION_ACCOUNT,
                account(7, "Member", "member@sportzone.local",
                        "0901234567", "Active"));
        fixture.sessionData.put(AppConstants.SESSION_ROLE_NAME, "User");
        fixture.sessionData.put(AppConstants.SESSION_FROZEN, Boolean.FALSE);
        return fixture;
    }

    private static Fixture validPostFixture() {
        Fixture fixture = loggedInFixture();
        fixture.sessionData.put(AppConstants.SESSION_CSRF_TOKEN, "valid-token");
        fixture.parameters.put(CsrfUtil.PARAMETER_NAME, "valid-token");
        return fixture;
    }

    private static Account account(int id, String fullName, String email,
            String phone, String status) {
        Account account = new Account();
        account.setAccountID(id);
        account.setRoleID(2);
        account.setRoleName("User");
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPhone(phone);
        account.setStatus(status);
        account.setCreatedAt(LocalDateTime.of(2026, 1, 1, 8, 0));
        return account;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        if (type == char.class) return (char) 0;
        return null;
    }

    private static final class TestProfileServlet extends ProfileServlet {

        TestProfileServlet(ProfileService service) {
            super(service);
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

    private static final class TestPasswordServlet extends PasswordServlet {

        TestPasswordServlet(ProfileService service) {
            super(service);
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

    private static final class FakeAccountDAO extends AccountDAO {

        private Account idResult;
        private int profileWrites;
        private int passwordWrites;
        private String passwordHash;

        int totalWrites() {
            return profileWrites + passwordWrites;
        }

        @Override
        public boolean existsByEmailExcludingId(String email, int accountID) {
            return false;
        }

        @Override
        public boolean existsByPhoneExcludingId(String phone, int accountID) {
            return false;
        }

        @Override
        public boolean updateProfile(int accountID, String fullName,
                String email, String phone) {
            profileWrites++;
            return true;
        }

        @Override
        public boolean updatePasswordHash(int accountID, String hash) {
            passwordWrites++;
            passwordHash = hash;
            return true;
        }

        @Override
        public Account findById(int accountID) {
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

        Fixture(String contextPath) {
            session = (HttpSession) Proxy.newProxyInstance(
                    Phase4ServletTest.class.getClassLoader(),
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
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    });

            request = (HttpServletRequest) Proxy.newProxyInstance(
                    Phase4ServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class},
                    (proxy, method, args) -> {
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
                                return dispatcher((String) args[0]);
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    });

            response = (HttpServletResponse) Proxy.newProxyInstance(
                    Phase4ServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, args) -> {
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
                    Phase4ServletTest.class.getClassLoader(),
                    new Class<?>[]{RequestDispatcher.class},
                    (proxy, method, args) -> {
                        if ("forward".equals(method.getName())) {
                            forwardedPath = path;
                        }
                        return defaultValue(method.getReturnType());
                    });
        }
    }
}
