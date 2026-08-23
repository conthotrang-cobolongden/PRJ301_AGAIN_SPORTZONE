package controller;

import dao.CourtDAO;
import dao.TimeSlotDAO;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Court;
import model.DateOption;
import model.TimeSlot;
import utils.Views;

/**
 * Kiem tra HTTP flow GET /court.
 *
 * Contract:
 * - Validate court ID.
 * - Chi cho xem Court active do CourtDAO tra ve.
 * - Gioi han ngay trong khoang hom nay den hom nay + 6.
 * - Sinh dung 7 DateOption.
 * - Load slot theo court va selected date.
 * - Danh dau slot da bat dau bang Clock.
 * - Bind dung JSP attributes.
 *
 * Tag: U8, U9
 */
public final class CourtDetailServletTest {

    private static final ZoneId TEST_ZONE
            = ZoneId.of("Asia/Ho_Chi_Minh");

    /*
     * 2026-08-23T03:30Z = 2026-08-23 10:30 tai Viet Nam.
     */
    private static final Clock TEST_CLOCK
            = Clock.fixed(
                    Instant.parse("2026-08-23T03:30:00Z"),
                    TEST_ZONE
            );

    private static final LocalDate TODAY
            = LocalDate.of(2026, 8, 23);

    private static final LocalDate LAST_ALLOWED_DATE
            = TODAY.plusDays(6);

    private CourtDetailServletTest() {
    }

    public static void main(String[] args) throws Exception {
        testValidRequestLoadsCourtScheduleAndDateOptions();
        testDateIsClampedToSevenDayWindow();
        testInvalidCourtIDReturns404BeforeDAO();
        testMissingCourtReturns404BeforeSlotQuery();
        testSQLExceptionBecomesServletException();
        testNullDependenciesAreRejected();

        System.out.println("CourtDetailServletTest PASSED");
    }

    /**
     * Request hop le phai bind day du contract cua court/detail.jsp.
     */
    private static void
            testValidRequestLoadsCourtScheduleAndDateOptions()
            throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put("id", "1");

        FakeCourtDAO courtDAO = new FakeCourtDAO();
        courtDAO.result = court(
                1,
                "Tennis Court 1",
                "Available"
        );

        FakeTimeSlotDAO timeSlotDAO = new FakeTimeSlotDAO();

        TimeSlot pastSlot = slot(
                101,
                1,
                TODAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Available"
        );

        TimeSlot futureSlot = slot(
                102,
                1,
                TODAY,
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                "Available"
        );

        timeSlotDAO.result.add(pastSlot);
        timeSlotDAO.result.add(futureSlot);

        Object servlet = createServlet(
                courtDAO,
                timeSlotDAO,
                TEST_CLOCK
        );

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                courtDAO.calls == 1
                && courtDAO.requestedCourtID == 1,
                "Servlet must load requested court exactly once"
        );

        require(
                timeSlotDAO.calls == 1,
                "Servlet must load slots exactly once"
        );

        require(
                timeSlotDAO.requestedCourtID == 1,
                "Slot query must use CourtID loaded from Court"
        );

        require(
                TODAY.equals(timeSlotDAO.requestedDate),
                "Missing date must default to today"
        );

        require(
                fixture.attributes.get("court")
                == courtDAO.result,
                "Servlet must expose loaded Court as court"
        );

        require(
                fixture.attributes.get("slotList")
                == timeSlotDAO.result,
                "Servlet must expose loaded slots as slotList"
        );

        require(
                TODAY.toString().equals(
                        fixture.attributes.get("selectedDate")
                ),
                "selectedDate must use ISO yyyy-MM-dd"
        );

        require(
                java.sql.Date.valueOf(TODAY).equals(
                        fixture.attributes.get("selectedDateObj")
                ),
                "selectedDateObj must be java.sql.Date for fmt:formatDate"
        );

        require(
                pastSlot.isPast(),
                "Slot starting before current time must be marked past"
        );

        require(
                !futureSlot.isPast(),
                "Future slot on today must remain not past"
        );

        requireDateOptions(
                fixture.attributes.get("dateOptions"),
                TODAY
        );

        require(
                Views.COURT_DETAIL.equals(
                        fixture.forwardedPath
                ),
                "Valid request must forward to Views.COURT_DETAIL"
        );

        require(
                fixture.errorStatus == -1,
                "Valid request must not send an error"
        );
    }

    /**
     * Date parameter phai duoc clamp thay vi cho phep xem ngoai
     * khoang seed 7 ngay.
     */
    private static void testDateIsClampedToSevenDayWindow()
            throws Exception {
        assertSelectedDate(
                "2026-08-01",
                TODAY
        );

        assertSelectedDate(
                "2026-09-30",
                LAST_ALLOWED_DATE
        );

        assertSelectedDate(
                "not-a-date",
                TODAY
        );

        assertSelectedDate(
                "   ",
                TODAY
        );

        assertSelectedDate(
                " 2026-08-25 ",
                LocalDate.of(2026, 8, 25)
        );
    }

    private static void assertSelectedDate(
            String submittedDate,
            LocalDate expectedDate
    ) throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put("id", "1");
        fixture.parameters.put("date", submittedDate);

        FakeCourtDAO courtDAO = new FakeCourtDAO();
        courtDAO.result = court(
                1,
                "Tennis Court 1",
                "Available"
        );

        FakeTimeSlotDAO timeSlotDAO
                = new FakeTimeSlotDAO();

        Object servlet = createServlet(
                courtDAO,
                timeSlotDAO,
                TEST_CLOCK
        );

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                expectedDate.equals(
                        timeSlotDAO.requestedDate
                ),
                "Submitted date must be clamped to "
                + expectedDate
        );

        require(
                expectedDate.toString().equals(
                        fixture.attributes.get("selectedDate")
                ),
                "selectedDate must match clamped date"
        );

        requireDateOptions(
                fixture.attributes.get("dateOptions"),
                expectedDate
        );
    }

    /**
     * ID thieu, khong phai so hoac khong duong deu la 404.
     */
    private static void testInvalidCourtIDReturns404BeforeDAO()
            throws Exception {
        String[] invalidValues = {
            null,
            "",
            "abc",
            "0",
            "-1",
            "999999999999999999999"
        };

        for (String invalidValue : invalidValues) {
            Fixture fixture = new Fixture();

            if (invalidValue != null) {
                fixture.parameters.put(
                        "id",
                        invalidValue
                );
            }

            FakeCourtDAO courtDAO
                    = new FakeCourtDAO();

            FakeTimeSlotDAO timeSlotDAO
                    = new FakeTimeSlotDAO();

            Object servlet = createServlet(
                    courtDAO,
                    timeSlotDAO,
                    TEST_CLOCK
            );

            invokeGet(
                    servlet,
                    fixture.request,
                    fixture.response
            );

            require(
                    fixture.errorStatus
                    == HttpServletResponse.SC_NOT_FOUND,
                    "Invalid court ID must return HTTP 404"
            );

            require(
                    courtDAO.calls == 0,
                    "Invalid court ID must return before CourtDAO"
            );

            require(
                    timeSlotDAO.calls == 0,
                    "Invalid court ID must return before TimeSlotDAO"
            );

            require(
                    fixture.forwardedPath == null,
                    "Invalid court ID must not forward"
            );
        }
    }

    /**
     * CourtDAO tra null nghia la san khong ton tai hoac inactive.
     */
    private static void testMissingCourtReturns404BeforeSlotQuery()
            throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put("id", "999");

        FakeCourtDAO courtDAO = new FakeCourtDAO();
        courtDAO.result = null;

        FakeTimeSlotDAO timeSlotDAO
                = new FakeTimeSlotDAO();

        Object servlet = createServlet(
                courtDAO,
                timeSlotDAO,
                TEST_CLOCK
        );

        invokeGet(
                servlet,
                fixture.request,
                fixture.response
        );

        require(
                fixture.errorStatus
                == HttpServletResponse.SC_NOT_FOUND,
                "Missing/inactive court must return HTTP 404"
        );

        require(
                courtDAO.calls == 1,
                "Valid numeric ID must query CourtDAO once"
        );

        require(
                timeSlotDAO.calls == 0,
                "Missing court must return before slot query"
        );

        require(
                fixture.forwardedPath == null,
                "Missing court must not forward"
        );
    }

    /**
     * SQLException phai duoc boc lai, khong hien truc tiep cho user.
     */
    private static void testSQLExceptionBecomesServletException()
            throws Exception {
        Fixture fixture = new Fixture();
        fixture.parameters.put("id", "1");

        FakeCourtDAO courtDAO = new FakeCourtDAO();

        SQLException databaseFailure
                = new SQLException("database unavailable");

        courtDAO.failure = databaseFailure;

        FakeTimeSlotDAO timeSlotDAO
                = new FakeTimeSlotDAO();

        Object servlet = createServlet(
                courtDAO,
                timeSlotDAO,
                TEST_CLOCK
        );

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
                timeSlotDAO.calls == 0,
                "CourtDAO failure must stop before slot query"
        );

        require(
                fixture.forwardedPath == null,
                "Failed request must not forward"
        );
    }

    /**
     * Servlet khong duoc ton tai voi dependency null.
     */
    private static void testNullDependenciesAreRejected()
            throws Exception {
        requireConstructorRejected(
                null,
                new FakeTimeSlotDAO(),
                TEST_CLOCK
        );

        requireConstructorRejected(
                new FakeCourtDAO(),
                null,
                TEST_CLOCK
        );

        requireConstructorRejected(
                new FakeCourtDAO(),
                new FakeTimeSlotDAO(),
                null
        );
    }

    private static void requireConstructorRejected(
            CourtDAO courtDAO,
            TimeSlotDAO timeSlotDAO,
            Clock clock
    ) throws Exception {
        Class<?> servletType = loadServletType();

        Constructor<?> constructor
                = servletType.getDeclaredConstructor(
                        CourtDAO.class,
                        TimeSlotDAO.class,
                        Clock.class
                );

        constructor.setAccessible(true);

        boolean rejected = false;

        try {
            constructor.newInstance(
                    courtDAO,
                    timeSlotDAO,
                    clock
            );
        } catch (InvocationTargetException exception) {
            rejected = exception.getCause()
                    instanceof IllegalArgumentException;
        }

        require(
                rejected,
                "CourtDetailServlet must reject null dependencies"
        );
    }

    /**
     * Kiem tra day 7 DateOption va ngay selected.
     */
    @SuppressWarnings("unchecked")
    private static void requireDateOptions(
            Object rawOptions,
            LocalDate expectedSelectedDate
    ) {
        require(
                rawOptions instanceof List,
                "dateOptions must be a List"
        );

        List<DateOption> options
                = (List<DateOption>) rawOptions;

        require(
                options.size() == 7,
                "dateOptions must contain today through today + 6"
        );

        DateOption first = options.get(0);
        DateOption last = options.get(6);

        require(
                TODAY.toString().equals(first.getValue()),
                "First DateOption must represent today"
        );

        require(
                "CN".equals(first.getWeekday()),
                "2026-08-23 must use Vietnamese Sunday label CN"
        );

        require(
                "23/08".equals(first.getDayMonth()),
                "First DateOption must use dd/MM"
        );

        require(
                LAST_ALLOWED_DATE.toString().equals(
                        last.getValue()
                ),
                "Last DateOption must represent today + 6"
        );

        int selectedCount = 0;

        for (DateOption option : options) {
            if (option.isSelected()) {
                selectedCount++;

                require(
                        expectedSelectedDate.toString().equals(
                                option.getValue()
                        ),
                        "Selected DateOption must match selectedDate"
                );
            }
        }

        require(
                selectedCount == 1,
                "Exactly one DateOption must be selected"
        );
    }

    private static Court court(
            int courtID,
            String name,
            String status
    ) {
        Court court = new Court();
        court.setCourtID(courtID);
        court.setCourtName(name);
        court.setStatus(status);
        court.setActive(true);
        return court;
    }

    private static TimeSlot slot(
            int timeSlotID,
            int courtID,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String status
    ) {
        TimeSlot slot = new TimeSlot();
        slot.setTimeSlotID(timeSlotID);
        slot.setCourtID(courtID);
        slot.setSlotDate(date);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(status);
        return slot;
    }

    /**
     * Reflection giu test compile trong vong RED.
     */
    private static Class<?> loadServletType() {
        try {
            return Class.forName(
                    "controller.CourtDetailServlet"
            );
        } catch (ClassNotFoundException exception) {
            throw new AssertionError(
                    "CourtDetailServlet must exist for GET /court",
                    exception
            );
        }
    }

    private static Object createServlet(
            CourtDAO courtDAO,
            TimeSlotDAO timeSlotDAO,
            Clock clock
    ) throws Exception {
        Class<?> servletType = loadServletType();

        Constructor<?> constructor
                = servletType.getDeclaredConstructor(
                        CourtDAO.class,
                        TimeSlotDAO.class,
                        Clock.class
                );

        constructor.setAccessible(true);

        return constructor.newInstance(
                courtDAO,
                timeSlotDAO,
                clock
        );
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

        private Court result;
        private SQLException failure;
        private int calls;
        private int requestedCourtID;

        @Override
        public Court findById(int courtID)
                throws SQLException {
            calls++;
            requestedCourtID = courtID;

            if (failure != null) {
                throw failure;
            }

            return result;
        }
    }

    private static final class FakeTimeSlotDAO
            extends TimeSlotDAO {

        private final List<TimeSlot> result
                = new ArrayList<>();

        private SQLException failure;
        private int calls;
        private int requestedCourtID;
        private LocalDate requestedDate;

        @Override
        public List<TimeSlot> listByCourtAndDate(
                int courtID,
                LocalDate slotDate
        ) throws SQLException {
            calls++;
            requestedCourtID = courtID;
            requestedDate = slotDate;

            if (failure != null) {
                throw failure;
            }

            return result;
        }
    }

    private static final class Fixture {

        private final Map<String, String> parameters
                = new HashMap<>();

        private final Map<String, Object> attributes
                = new HashMap<>();

        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private String forwardedPath;
        private int errorStatus = -1;

        Fixture() {
            request = (HttpServletRequest) Proxy.newProxyInstance(
                    CourtDetailServletTest.class.getClassLoader(),
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
                    CourtDetailServletTest.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, arguments) -> {
                        if ("sendError".equals(method.getName())) {
                            errorStatus = (Integer) arguments[0];
                            return null;
                        }

                        return defaultValue(
                                method.getReturnType()
                        );
                    }
            );
        }

        private RequestDispatcher dispatcher(String path) {
            return (RequestDispatcher) Proxy.newProxyInstance(
                    CourtDetailServletTest.class.getClassLoader(),
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