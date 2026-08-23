package controller;

import dao.CourtDAO;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Court;
import utils.Views;

/**
 * Kiem tra HTTP flow GET /courts bang servlet API proxy.
 *
 * Contract:
 * - Route la public GET.
 * - Filter chi chap nhan all, tennis va pickleball.
 * - Filter duoc trim va lowercase.
 * - Bind dung attribute ma court/list.jsp dang su dung.
 * - SQLException duoc boc trong ServletException.
 *
 * Tag: U7
 */
public final class CourtsServletTest {

    private CourtsServletTest() {
    }

    public static void main(String[] args) throws Exception {
        testMissingFilterDefaultsToAll();
        testTennisFilterIsNormalized();
        testPickleballFilterIsAccepted();
        testUnknownFilterDefaultsToAll();
        testSQLExceptionBecomesServletException();
        testNullDAOIsRejected();

        System.out.println("CourtsServletTest PASSED");
    }

    /**
     * GET /courts khong co query parameter phai hien tat ca san.
     */
    private static void testMissingFilterDefaultsToAll()
            throws Exception {
        Fixture fixture = new Fixture();
        FakeCourtDAO courtDAO = standardDAO();

        Object servlet = createServlet(courtDAO);

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                courtDAO.findAllCalls == 1,
                "GET /courts must load the court list exactly once"
        );

        require(
                "all".equals(courtDAO.lastTypeFilter),
                "Missing type filter must be normalized to all"
        );

        require(
                fixture.attributes.get("courtList")
                == courtDAO.findAllResult,
                "Servlet must expose DAO result as courtList"
        );

        require(
                Integer.valueOf(5).equals(
                        fixture.attributes.get("tennisCount")
                ),
                "Servlet must expose tennisCount"
        );

        require(
                Integer.valueOf(5).equals(
                        fixture.attributes.get("pickleballCount")
                ),
                "Servlet must expose pickleballCount"
        );

        require(
                "all".equals(
                        fixture.attributes.get("typeFilter")
                ),
                "Servlet must expose normalized typeFilter"
        );

        require(
                Views.COURT_LIST.equals(fixture.forwardedPath),
                "GET /courts must forward to Views.COURT_LIST"
        );
    }

    /**
     * Filter phai duoc trim va lowercase truoc khi goi DAO.
     */
    private static void testTennisFilterIsNormalized()
            throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put("type", "  TENNIS  ");

        FakeCourtDAO courtDAO = standardDAO();
        Object servlet = createServlet(courtDAO);

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                "tennis".equals(courtDAO.lastTypeFilter),
                "Tennis filter must be trimmed and lowercased"
        );

        require(
                "tennis".equals(
                        fixture.attributes.get("typeFilter")
                ),
                "JSP must receive normalized tennis typeFilter"
        );
    }

    private static void testPickleballFilterIsAccepted()
            throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put("type", "pickleball");

        FakeCourtDAO courtDAO = standardDAO();
        Object servlet = createServlet(courtDAO);

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                "pickleball".equals(courtDAO.lastTypeFilter),
                "Pickleball filter must be passed to CourtDAO"
        );

        require(
                "pickleball".equals(
                        fixture.attributes.get("typeFilter")
                ),
                "JSP must receive pickleball typeFilter"
        );
    }

    /**
     * Gia tri khong nam trong whitelist khong duoc dua thang vao DAO.
     */
    private static void testUnknownFilterDefaultsToAll()
            throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put(
                "type",
                "basketball' OR 1=1 --"
        );

        FakeCourtDAO courtDAO = standardDAO();
        Object servlet = createServlet(courtDAO);

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                "all".equals(courtDAO.lastTypeFilter),
                "Unknown type filter must default to all"
        );

        require(
                "all".equals(
                        fixture.attributes.get("typeFilter")
                ),
                "JSP must never receive an unknown filter"
        );
    }

    /**
     * Loi persistence khong duoc nuot hoac hien SQL message ra response.
     */
    private static void testSQLExceptionBecomesServletException()
            throws Exception {
        Fixture fixture = new Fixture();
        FakeCourtDAO courtDAO = standardDAO();

        SQLException databaseFailure
                = new SQLException("database unavailable");

        courtDAO.findAllFailure = databaseFailure;

        Object servlet = createServlet(courtDAO);

        boolean wrapped = false;

        try {
            invokeGet(
                    servlet,
                    fixture.request,
                    fixture.response
            );
        } catch (ServletException exception) {
            wrapped = exception.getCause() == databaseFailure;
        }

        require(
                wrapped,
                "CourtDAO SQLException must become ServletException"
        );

        require(
                fixture.forwardedPath == null,
                "Failed request must not forward partial data to JSP"
        );
    }

    /**
     * Dependency injection constructor phai chan null de test va
     * production khong tao servlet o trang thai khong hop le.
     */
    private static void testNullDAOIsRejected()
            throws Exception {
        Class<?> servletType = loadServletType();

        Constructor<?> constructor
                = servletType.getDeclaredConstructor(
                        CourtDAO.class
                );

        constructor.setAccessible(true);

        boolean rejected = false;

        try {
            constructor.newInstance(
                    new Object[]{null}
            );
        } catch (InvocationTargetException exception) {
            rejected
                    = exception.getCause()
                    instanceof IllegalArgumentException;
        }

        require(
                rejected,
                "CourtsServlet must reject a null CourtDAO"
        );
    }

    /**
     * Tao fake data dung cho trang danh sach.
     */
    private static FakeCourtDAO standardDAO() {
        FakeCourtDAO courtDAO = new FakeCourtDAO();

        Court tennis = new Court();
        tennis.setCourtID(1);
        tennis.setCourtName("Tennis Court 1");
        tennis.setTypeName("Tennis");
        tennis.setTypeSlug("tennis");

        Court pickleball = new Court();
        pickleball.setCourtID(6);
        pickleball.setCourtName("Pickleball Court 1");
        pickleball.setTypeName("Pickleball");
        pickleball.setTypeSlug("pickleball");

        courtDAO.findAllResult.add(tennis);
        courtDAO.findAllResult.add(pickleball);
        courtDAO.tennisCount = 5;
        courtDAO.pickleballCount = 5;

        return courtDAO;
    }

    /**
     * Dung reflection de test van compile trong vong RED
     * khi CourtsServlet chua ton tai.
     */
    private static Class<?> loadServletType() {
        try {
            return Class.forName(
                    "controller.CourtsServlet"
            );
        } catch (ClassNotFoundException exception) {
            throw new AssertionError(
                    "CourtsServlet must exist for GET /courts",
                    exception
            );
        }
    }

    /**
     * Tao servlet bang constructor nhan CourtDAO.
     *
     * Constructor nay package-private trong production:
     * - Default constructor dung khi Tomcat khoi tao.
     * - Constructor CourtDAO dung de test dependency.
     */
    private static Object createServlet(CourtDAO courtDAO)
            throws Exception {
        Class<?> servletType = loadServletType();

        Constructor<?> constructor
                = servletType.getDeclaredConstructor(
                        CourtDAO.class
                );

        constructor.setAccessible(true);

        return constructor.newInstance(courtDAO);
    }

    /**
     * Goi protected doGet bang reflection.
     */
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

    /**
     * CourtDAO gia de kiem tra servlet ma khong can database.
     */
    private static final class FakeCourtDAO extends CourtDAO {

        private final List<Court> findAllResult
                = new ArrayList<>();

        private int tennisCount;
        private int pickleballCount;
        private int findAllCalls;
        private String lastTypeFilter;
        private SQLException findAllFailure;

        @Override
        public List<Court> findAll(String typeFilter)
                throws SQLException {
            findAllCalls++;
            lastTypeFilter = typeFilter;

            if (findAllFailure != null) {
                throw findAllFailure;
            }

            return findAllResult;
        }

        @Override
        public int countByType(String typeFilter)
                throws SQLException {
            if ("tennis".equals(typeFilter)) {
                return tennisCount;
            }

            if ("pickleball".equals(typeFilter)) {
                return pickleballCount;
            }

            return 0;
        }
    }

    /**
     * Request/response fixture toi thieu cho GET /courts.
     */
    private static final class Fixture {

        private final java.util.Map<String, String> parameters
                = new java.util.HashMap<>();

        private final java.util.Map<String, Object> attributes
                = new java.util.HashMap<>();

        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private String forwardedPath;

        Fixture() {
            request = (HttpServletRequest) Proxy.newProxyInstance(
                    CourtsServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class},
                    (proxy, method, arguments) -> {
                        switch (method.getName()) {
                            case "getParameter":
                                return parameters.get(
                                        (String) arguments[0]
                                );

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

                            default:
                                return defaultValue(
                                        method.getReturnType()
                                );
                        }
                    }
            );

            response = (HttpServletResponse) Proxy.newProxyInstance(
                    CourtsServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, arguments) -> defaultValue(
                            method.getReturnType()
                    )
            );
        }

        private RequestDispatcher dispatcher(String path) {
            return (RequestDispatcher) Proxy.newProxyInstance(
                    CourtsServletTest.class.getClassLoader(),
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