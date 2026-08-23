package service;

import dao.BookingDAO;
import dao.ServiceDAO;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingSlot;
import model.Service;

/**
 * Batch test business rules + transaction Phase 5.
 *
 * Khong ket noi database that.
 */
public final class BookingServiceTest {

    private static final Clock FIXED_CLOCK
            = Clock.fixed(
                    Instant.parse("2026-08-23T01:00:00Z"),
                    ZoneId.of("Asia/Ho_Chi_Minh")
            );

    private BookingServiceTest() {
    }

    public static void main(String[] args)
            throws Exception {
        shouldCreateBookingAndSnapshotPrices();
        shouldCreateBookingWithoutServices();
        shouldRejectFrozenAccount();
        shouldRejectInvalidSlotAndCourtConditions();
        shouldRejectUnavailableService();
        shouldRollbackWhenSlotUpdateLosesRace();
        shouldConvertUniqueConflictToBusinessFailure();
        shouldRollbackAndRethrowUnexpectedSqlFailure();
        shouldValidateInputBeforeOpeningTransaction();
        shouldDelegateOwnerScopedReads();

        System.out.println("BookingServiceTest PASSED");
    }

    private static void shouldCreateBookingAndSnapshotPrices()
            throws Exception {
        Fixture fixture = fixture();

        fixture.serviceDAO.services.put(
                1,
                activeService(1, "50000")
        );

        LinkedHashMap<Integer, Integer> selections
                = new LinkedHashMap<>();

        selections.put(1, 2);

        BookingService.CreateBookingResult result
                = fixture.service.create(
                        2,
                        100,
                        selections,
                        "  Can muon vot  "
                );

        require(
                result.isSuccess(),
                "Booking hop le phai thanh cong"
        );

        require(
                result.getBookingID() == 88,
                "Phai tra ve generated BookingID"
        );

        require(
                fixture.connection.committed,
                "Booking hop le phai commit"
        );

        require(
                !fixture.connection.rolledBack,
                "Booking hop le khong duoc rollback"
        );

        require(
                fixture.connection.closed,
                "Connection phai duoc dong"
        );

        Booking inserted
                = fixture.bookingDAO.insertedBooking;

        require(
                inserted != null,
                "Phai insert Booking"
        );

        require(
                inserted.getAccountID() == 2,
                "Booking phai dung AccountID"
        );

        require(
                inserted.getTimeSlotID() == 100,
                "Booking chi ghi TimeSlotID"
        );

        assertMoney(
                "150000",
                inserted.getCourtPriceAtOrder(),
                "Phai snapshot gia san"
        );

        assertMoney(
                "100000",
                inserted.getServiceTotalAtOrder(),
                "Service total = price x quantity"
        );

        assertMoney(
                "250000",
                inserted.getTotalAmount(),
                "Total = court + services"
        );

        require(
                "Pending".equals(inserted.getStatus()),
                "Booking moi phai la Pending"
        );

        require(
                "Can muon vot".equals(
                        inserted.getNotes()
                ),
                "Notes phai duoc trim"
        );

        require(
                fixture.bookingDAO.insertedLines.size() == 1,
                "Phai insert mot BookingServices line"
        );

        model.BookingService insertedLine
                = fixture.bookingDAO.insertedLines.get(0);

        require(
                insertedLine.getBookingID() == 88,
                "Service line phai dung generated BookingID"
        );

        require(
                insertedLine.getServiceID() == 1
                && insertedLine.getQuantity() == 2,
                "Service line phai giu ID va quantity"
        );

        assertMoney(
                "50000",
                insertedLine.getPriceAtOrder(),
                "Phai snapshot service price"
        );

        require(
                fixture.bookingDAO.markedSlotID == 100,
                "Slot phai duoc chuyen sang Booked"
        );
    }

    private static void shouldCreateBookingWithoutServices()
            throws Exception {
        Fixture fixture = fixture();

        BookingService.CreateBookingResult result
                = fixture.service.create(
                        2,
                        100,
                        Collections.emptyMap(),
                        null
                );

        require(
                result.isSuccess(),
                "Booking khong chon service van hop le"
        );

        assertMoney(
                "0",
                fixture.bookingDAO.insertedBooking
                        .getServiceTotalAtOrder(),
                "Khong chon service thi service total = 0"
        );

        assertMoney(
                "150000",
                fixture.bookingDAO.insertedBooking
                        .getTotalAmount(),
                "Khong service thi total = court price"
        );

        require(
                fixture.bookingDAO.insertedLines.isEmpty(),
                "Khong service thi khong insert BookingServices"
        );
    }

    private static void shouldRejectFrozenAccount()
            throws Exception {
        Fixture fixture = fixture();
        fixture.bookingDAO.accountStatus = "Frozen";

        BookingService.CreateBookingResult result
                = fixture.service.create(
                        2,
                        100,
                        null,
                        null
                );

        assertFailure(
                result,
                BookingService.ACCOUNT_NOT_ACTIVE
        );

        require(
                fixture.connection.rolledBack,
                "Frozen account phai rollback"
        );

        require(
                fixture.bookingDAO.insertedBooking == null,
                "Frozen account khong duoc insert"
        );
    }

    private static void shouldRejectInvalidSlotAndCourtConditions()
            throws Exception {
        Fixture missingSlot = fixture();
        missingSlot.bookingDAO.slot = null;

        assertFailure(
                missingSlot.service.create(
                        2, 100, null, null
                ),
                BookingService.SLOT_NOT_FOUND
        );

        Fixture bookedSlot = fixture();
        bookedSlot.bookingDAO.slot
                .setSlotStatus("Booked");

        assertFailure(
                bookedSlot.service.create(
                        2, 100, null, null
                ),
                BookingService.SLOT_UNAVAILABLE
        );

        Fixture unavailableCourt = fixture();
        unavailableCourt.bookingDAO.slot
                .setCourtStatus("Maintenance");

        assertFailure(
                unavailableCourt.service.create(
                        2, 100, null, null
                ),
                BookingService.COURT_UNAVAILABLE
        );

        Fixture expiredSlot = fixture();
        expiredSlot.bookingDAO.slot.setSlotDate(
                LocalDate.of(2026, 8, 23)
        );
        expiredSlot.bookingDAO.slot.setStartTime(
                LocalTime.of(7, 0)
        );

        assertFailure(
                expiredSlot.service.create(
                        2, 100, null, null
                ),
                BookingService.SLOT_EXPIRED
        );

        require(
                missingSlot.connection.rolledBack
                && bookedSlot.connection.rolledBack
                && unavailableCourt.connection.rolledBack
                && expiredSlot.connection.rolledBack,
                "Moi business failure trong transaction phai rollback"
        );
    }

    private static void shouldRejectUnavailableService()
            throws Exception {
        Fixture fixture = fixture();

        Map<Integer, Integer> selections
                = Collections.singletonMap(999, 1);

        BookingService.CreateBookingResult result
                = fixture.service.create(
                        2,
                        100,
                        selections,
                        null
                );

        assertFailure(
                result,
                BookingService.SERVICE_UNAVAILABLE
        );

        require(
                fixture.connection.rolledBack,
                "Service khong ton tai phai rollback"
        );

        require(
                fixture.bookingDAO.insertedBooking == null,
                "Service khong ton tai khong duoc insert booking"
        );
    }

    private static void shouldRollbackWhenSlotUpdateLosesRace()
            throws Exception {
        Fixture fixture = fixture();
        fixture.bookingDAO.markSlotResult = false;

        BookingService.CreateBookingResult result
                = fixture.service.create(
                        2,
                        100,
                        null,
                        null
                );

        assertFailure(
                result,
                BookingService.SLOT_UNAVAILABLE
        );

        require(
                fixture.connection.rolledBack,
                "Mat race update slot phai rollback booking"
        );

        require(
                !fixture.connection.committed,
                "Mat race khong duoc commit"
        );
    }

    private static void shouldConvertUniqueConflictToBusinessFailure()
            throws Exception {
        Fixture fixture = fixture();

        fixture.bookingDAO.insertFailure
                = new SQLException(
                        "Duplicate index UQ_Bookings_Slot_Active",
                        "23000",
                        2601
                );

        BookingService.CreateBookingResult result
                = fixture.service.create(
                        2,
                        100,
                        null,
                        null
                );

        assertFailure(
                result,
                BookingService.SLOT_UNAVAILABLE
        );

        require(
                fixture.connection.rolledBack,
                "Unique conflict phai rollback"
        );
    }

    private static void shouldRollbackAndRethrowUnexpectedSqlFailure()
            throws Exception {
        Fixture fixture = fixture();

        SQLException databaseFailure
                = new SQLException(
                        "Database offline",
                        "08001",
                        0
                );

        fixture.bookingDAO.insertFailure
                = databaseFailure;

        boolean rethrown = false;

        try {
            fixture.service.create(
                    2,
                    100,
                    null,
                    null
            );
        } catch (SQLException exception) {
            rethrown = exception == databaseFailure;
        }

        require(
                rethrown,
                "SQL loi bat ngo phai duoc nem lai"
        );

        require(
                fixture.connection.rolledBack,
                "SQL loi bat ngo van phai rollback"
        );
    }

    private static void shouldValidateInputBeforeOpeningTransaction()
            throws Exception {
        Fixture invalidID = fixture();

        BookingService.CreateBookingResult invalidIDResult
                = invalidID.service.create(
                        0,
                        100,
                        null,
                        null
                );

        assertFailure(
                invalidIDResult,
                BookingService.INVALID_INPUT
        );

        require(
                !invalidID.bookingDAO.connectionOpened,
                "ID sai phai bi chan truoc khi mo DB"
        );

        Fixture invalidQuantity = fixture();

        BookingService.CreateBookingResult quantityResult
                = invalidQuantity.service.create(
                        2,
                        100,
                        Collections.singletonMap(1, 0),
                        null
                );

        assertFailure(
                quantityResult,
                BookingService.INVALID_INPUT
        );

        require(
                !invalidQuantity.bookingDAO.connectionOpened,
                "Quantity sai phai bi chan truoc khi mo DB"
        );

        Fixture longNotes = fixture();

        StringBuilder notes = new StringBuilder();

        for (int index = 0; index < 256; index++) {
            notes.append('x');
        }

        BookingService.CreateBookingResult notesResult
                = longNotes.service.create(
                        2,
                        100,
                        null,
                        notes.toString()
                );

        assertFailure(
                notesResult,
                BookingService.INVALID_INPUT
        );

        require(
                !longNotes.bookingDAO.connectionOpened,
                "Notes qua dai phai bi chan truoc DB"
        );
    }

    private static void shouldDelegateOwnerScopedReads()
            throws Exception {
        Fixture fixture = fixture();

        Booking ownedBooking = new Booking();
        ownedBooking.setBookingID(77);
        ownedBooking.setAccountID(2);

        fixture.bookingDAO.myBookings.add(
                ownedBooking
        );
        fixture.bookingDAO.detail = ownedBooking;

        model.BookingService line
                = new model.BookingService();

        line.setBookingID(77);
        line.setServiceID(1);

        fixture.bookingDAO.detailLines.add(line);

        List<Booking> bookings
                = fixture.service.listMine(2);

        require(
                bookings.size() == 1,
                "U11 phai delegate list owner"
        );

        BookingService.BookingDetail detail
                = fixture.service.getMine(77, 2);

        require(
                detail != null
                && detail.getBooking() == ownedBooking
                && detail.getServiceLines().size() == 1,
                "U12 phai tra booking + service lines"
        );

        require(
                fixture.bookingDAO.lastReadAccountID == 2
                && fixture.bookingDAO.lastReadBookingID == 77,
                "U11/U12 phai truyen AccountID vao DAO"
        );

        fixture.bookingDAO.detail = null;

        require(
                fixture.service.getMine(999, 2) == null,
                "Booking khong thuoc owner phai tra null"
        );
    }

    private static Fixture fixture() {
        ConnectionState connection
                = new ConnectionState();

        FakeBookingDAO bookingDAO
                = new FakeBookingDAO(connection);

        FakeServiceDAO serviceDAO
                = new FakeServiceDAO();

        BookingSlot slot = new BookingSlot();

        slot.setTimeSlotID(100);
        slot.setCourtID(1);
        slot.setSlotDate(
                LocalDate.of(2026, 8, 24)
        );
        slot.setStartTime(
                LocalTime.of(10, 0)
        );
        slot.setEndTime(
                LocalTime.of(11, 0)
        );
        slot.setSlotStatus("Available");
        slot.setCourtStatus("Available");
        slot.setCourtActive(true);
        slot.setCourtTypeActive(true);
        slot.setCourtPrice(
                new BigDecimal("150000")
        );

        bookingDAO.slot = slot;

        BookingService service
                = new BookingService(
                        bookingDAO,
                        serviceDAO,
                        FIXED_CLOCK
                );

        return new Fixture(
                service,
                bookingDAO,
                serviceDAO,
                connection
        );
    }

    private static Service activeService(
            int serviceID,
            String price
    ) {
        Service service = new Service();

        service.setServiceID(serviceID);
        service.setServiceName(
                "Service " + serviceID
        );
        service.setPrice(
                new BigDecimal(price)
        );
        service.setActive(true);

        return service;
    }

    private static void assertFailure(
            BookingService.CreateBookingResult result,
            String expectedCode
    ) {
        require(
                !result.isSuccess(),
                "Expected booking failure: "
                + expectedCode
        );

        require(
                expectedCode.equals(
                        result.getErrorCode()
                ),
                "Sai error code | expected="
                + expectedCode
                + ", actual="
                + result.getErrorCode()
        );

        require(
                result.getErrorMessage() != null
                && !result.getErrorMessage().trim()
                        .isEmpty(),
                "Business failure phai co message"
        );
    }

    private static void assertMoney(
            String expected,
            BigDecimal actual,
            String message
    ) {
        require(
                actual != null
                && new BigDecimal(expected)
                        .compareTo(actual) == 0,
                message
                + " | expected="
                + expected
                + ", actual="
                + actual
        );
    }

    private static void require(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new AssertionError(message);
        }
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
            return (char) 0;
        }

        return null;
    }

    private static final class Fixture {

        private final BookingService service;
        private final FakeBookingDAO bookingDAO;
        private final FakeServiceDAO serviceDAO;
        private final ConnectionState connection;

        private Fixture(
                BookingService service,
                FakeBookingDAO bookingDAO,
                FakeServiceDAO serviceDAO,
                ConnectionState connection
        ) {
            this.service = service;
            this.bookingDAO = bookingDAO;
            this.serviceDAO = serviceDAO;
            this.connection = connection;
        }
    }

    private static final class FakeBookingDAO
            extends BookingDAO {

        private final ConnectionState connection;
        private String accountStatus = "Active";
        private BookingSlot slot;
        private Booking insertedBooking;
        private final List<model.BookingService>
                insertedLines = new ArrayList<>();
        private boolean markSlotResult = true;
        private int markedSlotID;
        private SQLException insertFailure;
        private boolean connectionOpened;
        private final List<Booking> myBookings
                = new ArrayList<>();
        private Booking detail;
        private final List<model.BookingService>
                detailLines = new ArrayList<>();
        private int lastReadAccountID;
        private int lastReadBookingID;

        private FakeBookingDAO(
                ConnectionState connection
        ) {
            this.connection = connection;
        }

        @Override
        public Connection openConnection() {
            connectionOpened = true;
            return connection.proxy;
        }

        @Override
        public String findAccountStatusForUpdate(
                Connection ignored,
                int accountID
        ) {
            return accountStatus;
        }

        @Override
        public BookingSlot findSlotForUpdate(
                Connection ignored,
                int timeSlotID
        ) {
            return slot;
        }

        @Override
        public int insertBooking(
                Connection ignored,
                Booking booking
        ) throws SQLException {
            if (insertFailure != null) {
                throw insertFailure;
            }

            insertedBooking = booking;
            return 88;
        }

        @Override
        public void insertBookingService(
                Connection ignored,
                model.BookingService serviceLine
        ) {
            insertedLines.add(serviceLine);
        }

        @Override
        public boolean markSlotBooked(
                Connection ignored,
                int timeSlotID
        ) {
            markedSlotID = timeSlotID;
            return markSlotResult;
        }

        @Override
        public List<Booking> findByAccountID(
                int accountID
        ) {
            lastReadAccountID = accountID;
            return myBookings;
        }

        @Override
        public Booking findDetailByIDAndAccountID(
                int bookingID,
                int accountID
        ) {
            lastReadBookingID = bookingID;
            lastReadAccountID = accountID;
            return detail;
        }

        @Override
        public List<model.BookingService>
                findServicesByBookingIDAndAccountID(
                        int bookingID,
                        int accountID
                ) {
            lastReadBookingID = bookingID;
            lastReadAccountID = accountID;
            return detailLines;
        }
    }

    private static final class FakeServiceDAO
            extends ServiceDAO {

        private final Map<Integer, Service> services
                = new LinkedHashMap<>();

        @Override
        public Service findActiveByID(
                Connection ignored,
                int serviceID
        ) {
            return services.get(serviceID);
        }
    }

    private static final class ConnectionState
            implements InvocationHandler {

        private boolean autoCommit = true;
        private boolean committed;
        private boolean rolledBack;
        private boolean closed;

        private final Connection proxy
                = (Connection) Proxy.newProxyInstance(
                        Connection.class.getClassLoader(),
                        new Class<?>[]{Connection.class},
                        this
                );

        @Override
        public Object invoke(
                Object proxyObject,
                Method method,
                Object[] arguments
        ) {
            String name = method.getName();

            if ("setAutoCommit".equals(name)) {
                autoCommit = (Boolean) arguments[0];
                return null;
            }

            if ("getAutoCommit".equals(name)) {
                return autoCommit;
            }

            if ("commit".equals(name)) {
                committed = true;
                return null;
            }

            if ("rollback".equals(name)) {
                rolledBack = true;
                return null;
            }

            if ("close".equals(name)) {
                closed = true;
                return null;
            }

            if ("isClosed".equals(name)) {
                return closed;
            }

            if ("isWrapperFor".equals(name)) {
                return false;
            }

            if ("unwrap".equals(name)) {
                return null;
            }

            if ("toString".equals(name)) {
                return "FakeBookingConnection";
            }

            return defaultValue(
                    method.getReturnType()
            );
        }
    }
}