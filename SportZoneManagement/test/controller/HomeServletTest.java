package controller;

import dao.CourtDAO;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.AppConstants;
import utils.Views;

/**
 * Kiem tra HomeServlet lay so san that tu CourtDAO.
 *
 * Contract:
 * - Dem Tennis va Pickleball qua CourtDAO.
 * - Bind tennisCourtCount va pickleballCourtCount.
 * - Bao dam CSRF token ton tai cho layout/form dung chung.
 * - Forward toi Views.HOME.
 * - SQLException duoc boc trong ServletException.
 *
 * Tag: U19
 */
public final class HomeServletTest {

    private HomeServletTest() {
    }

    public static void main(String[] args) throws Exception {
        testGetLoadsRealCourtCountsAndForwards();
        testSQLExceptionBecomesServletException();
        testNullDAOIsRejected();

        System.out.println("HomeServletTest PASSED");
    }

    /**
     * Dung gia tri 7/4 thay vi seed 5/5 de test khong the pass
     * neu servlet van dang hard-code.
     */
    private static void testGetLoadsRealCourtCountsAndForwards()
            throws Exception {
        Fixture fixture = new Fixture();

        FakeCourtDAO courtDAO = new FakeCourtDAO();
        courtDAO.tennisCount = 7;
        courtDAO.pickleballCount = 4;

        Object servlet = createServlet(courtDAO);

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                courtDAO.calls == 2,
                "HomeServlet must query both court types"
        );

        require(
                "tennis".equals(courtDAO.requestedTypes.get(0)),
                "First count query must request tennis"
        );

        require(
                "pickleball".equals(courtDAO.requestedTypes.get(1)),
                "Second count query must request pickleball"
        );

        require(
                Integer.valueOf(7).equals(
                        fixture.attributes.get("tennisCourtCount")
                ),
                "HomeServlet must bind DAO Tennis count"
        );

        require(
                Integer.valueOf(4).equals(
                        fixture.attributes.get(
                                "pickleballCourtCount"
                        )
                ),
                "HomeServlet must bind DAO Pickleball count"
        );

        Object csrfToken = fixture.sessionData.get(
                AppConstants.SESSION_CSRF_TOKEN
        );

        require(
                csrfToken instanceof String
                && !((String) csrfToken).isEmpty(),
                "GET /home must ensure a CSRF token"
        );

        require(
                Views.HOME.equals(fixture.forwardedPath),
                "GET /home must forward to Views.HOME"
        );
    }

    /**
     * Loi DB khong duoc chuyen thanh count 0 hoac hard-code 5.
     */
    private static void testSQLExceptionBecomesServletException()
            throws Exception {
        Fixture fixture = new Fixture();

        SQLException databaseFailure
                = new SQLException("database unavailable");

        FakeCourtDAO courtDAO = new FakeCourtDAO();
        courtDAO.failure = databaseFailure;

        Object servlet = createServlet(courtDAO);

        boolean wrapped = false;

        try {
            invokeGet(
                    servlet,
                    fixture.request,
                    fixture.response
            );
        } catch (ServletException exception) {
            wrapped = exception.getCause()
                    == databaseFailure;
        }

        require(
                wrapped,
                "CourtDAO SQLException must become ServletException"
        );

        require(
                fixture.forwardedPath == null,
                "Database failure must not forward partial home data"
        );

        require(
                !fixture.attributes.containsKey(
                        "tennisCourtCount"
                ),
                "Database failure must not bind a fake Tennis count"
        );

        require(
                !fixture.attributes.containsKey(
                        "pickleballCourtCount"
                ),
                "Database failure must not bind a fake Pickleball count"
        );
    }

    private static void testNullDAOIsRejected()
            throws Exception {
        Constructor<?> constructor
                = requireDAOConstructor();

        boolean rejected = false;

        try {
            constructor.newInstance(
                    new Object[]{null}
            );
        } catch (InvocationTargetException exception) {
            rejected = exception.getCause()
                    instanceof IllegalArgumentException;
        }

        require(
                rejected,
                "HomeServlet must reject a null CourtDAO"
        );
    }

    /**
     * Constructor chua ton tai o vong RED.
     */
    private static Constructor<?> requireDAOConstructor() {
        try {
            Constructor<?> constructor
                    = HomeServlet.class.getDeclaredConstructor(
                            CourtDAO.class
                    );

            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException exception) {
            throw new AssertionError(
                    "HomeServlet must accept CourtDAO for real court counts",
                    exception
            );
        }
    }

    private static Object createServlet(CourtDAO courtDAO)
            throws Exception {
        return requireDAOConstructor().newInstance(courtDAO);
    }

    private static void invokeGet(
            Object servlet,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Method doGet = servlet.getClass().getDeclaredMethod(
                "doGet",
                HttpServletRequest.class,
                HttpServletResponse.class
        );

        doGet.setAccessible(true);

        try {
            doGet.invoke(
                    servlet,
                    request,
                    response
            );
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof Exception) {
                throw (Exception) cause;
            }

            if (cause instanceof Error) {
                throw (Error) cause;
            }

            throw exception;
        }
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

    private static void require(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class FakeCourtDAO
            extends CourtDAO {

        private final java.util.List<String> requestedTypes
                = new java.util.ArrayList<>();

        private int tennisCount;
        private int pickleballCount;
        private int calls;
        private SQLException failure;

        @Override
        public int countByType(String typeFilter)
                throws SQLException {
            calls++;
            requestedTypes.add(typeFilter);

            if (failure != null) {
                throw failure;
            }

            if ("tennis".equals(typeFilter)) {
                return tennisCount;
            }

            if ("pickleball".equals(typeFilter)) {
                return pickleballCount;
            }

            return 0;
        }
    }

    private static final class Fixture {

        private final Map<String, Object> sessionData
                = new HashMap<>();

        private final Map<String, Object> attributes
                = new HashMap<>();

        private final HttpSession session;
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private String forwardedPath;

        Fixture() {
            session = (HttpSession) Proxy.newProxyInstance(
                    HomeServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpSession.class},
                    (proxy, method, arguments) -> {
                        switch (method.getName()) {
                            case "getAttribute":
                                return sessionData.get(
                                        (String) arguments[0]
                                );

                            case "setAttribute":
                                sessionData.put(
                                        (String) arguments[0],
                                        arguments[1]
                                );
                                return null;

                            default:
                                return defaultValue(
                                        method.getReturnType()
                                );
                        }
                    }
            );

            request = (HttpServletRequest) Proxy.newProxyInstance(
                    HomeServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class},
                    (proxy, method, arguments) -> {
                        switch (method.getName()) {
                            case "getSession":
                                return session;

                            case "setAttribute":
                                attributes.put(
                                        (String) arguments[0],
                                        arguments[1]
                                );
                                return null;

                            case "getAttribute":
                                return attributes.get(
                                        (String) arguments[0]
                                );

                            case "getRequestDispatcher":
                                return dispatcher(
                                        (String) arguments[0]
                                );

                            case "setCharacterEncoding":
                                return null;

                            default:
                                return defaultValue(
                                        method.getReturnType()
                                );
                        }
                    }
            );

            response = (HttpServletResponse) Proxy.newProxyInstance(
                    HomeServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, arguments) -> defaultValue(
                            method.getReturnType()
                    )
            );
        }

        private RequestDispatcher dispatcher(String path) {
            return (RequestDispatcher) Proxy.newProxyInstance(
                    HomeServletTest.class.getClassLoader(),
                    new Class<?>[]{RequestDispatcher.class},
                    (proxy, method, arguments) -> {
                        if ("forward".equals(method.getName())) {
                            forwardedPath = path;
                        }

                        return defaultValue(
                                method.getReturnType()
                        );
                    }
            );
        }
    }
}