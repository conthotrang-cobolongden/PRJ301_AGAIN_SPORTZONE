package service;

import dao.BookingDAO;
import dao.ServiceDAO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Booking;
import model.BookingSlot;
import model.Service;

/**
 * Business rules cho U10, U11, U12.
 *
 * User khong co chuc nang tu huy booking.
 */
public class BookingService {

    public static final String INVALID_INPUT
            = "INVALID_INPUT";
    public static final String ACCOUNT_NOT_ACTIVE
            = "ACCOUNT_NOT_ACTIVE";
    public static final String SLOT_NOT_FOUND
            = "SLOT_NOT_FOUND";
    public static final String SLOT_UNAVAILABLE
            = "SLOT_UNAVAILABLE";
    public static final String SLOT_EXPIRED
            = "SLOT_EXPIRED";
    public static final String COURT_UNAVAILABLE
            = "COURT_UNAVAILABLE";
    public static final String SERVICE_UNAVAILABLE
            = "SERVICE_UNAVAILABLE";

    private static final int MAX_SERVICE_QUANTITY = 20;
    private static final int MAX_NOTES_LENGTH = 255;

    private final BookingDAO bookingDAO;
    private final ServiceDAO serviceDAO;
    private final Clock clock;

    public BookingService() {
        this(
                new BookingDAO(),
                new ServiceDAO(),
                Clock.systemDefaultZone()
        );
    }

    public BookingService(
            BookingDAO bookingDAO,
            ServiceDAO serviceDAO,
            Clock clock
    ) {
        if (bookingDAO == null) {
            throw new IllegalArgumentException(
                    "BookingDAO must not be null"
            );
        }

        if (serviceDAO == null) {
            throw new IllegalArgumentException(
                    "ServiceDAO must not be null"
            );
        }

        if (clock == null) {
            throw new IllegalArgumentException(
                    "Clock must not be null"
            );
        }

        this.bookingDAO = bookingDAO;
        this.serviceDAO = serviceDAO;
        this.clock = clock;
    }

    /**
     * U10: tao booking Pending trong mot transaction.
     *
     * serviceQuantities:
     * key   = ServiceID
     * value = Quantity
     */
    public CreateBookingResult create(
            int accountID,
            int timeSlotID,
            Map<Integer, Integer> serviceQuantities,
            String notes
    ) throws SQLException {
        if (accountID <= 0 || timeSlotID <= 0) {
            return CreateBookingResult.failure(
                    INVALID_INPUT,
                    "Thông tin đặt sân không hợp lệ."
            );
        }

        final String normalizedNotes;
        final Map<Integer, Integer> normalizedServices;

        try {
            normalizedNotes = normalizeNotes(notes);
            normalizedServices = normalizeServices(
                    serviceQuantities
            );
        } catch (BookingRuleException exception) {
            return CreateBookingResult.failure(
                    exception.getErrorCode(),
                    exception.getMessage()
            );
        }

        try (Connection connection
                = bookingDAO.openConnection()) {

            connection.setAutoCommit(false);

            try {
                String accountStatus
                        = bookingDAO
                                .findAccountStatusForUpdate(
                                        connection,
                                        accountID
                                );

                if (!"Active".equals(accountStatus)) {
                    reject(
                            ACCOUNT_NOT_ACTIVE,
                            "Tài khoản không ở trạng thái Active nên không thể đặt sân."
                    );
                }

                BookingSlot slot
                        = bookingDAO.findSlotForUpdate(
                                connection,
                                timeSlotID
                        );

                if (slot == null) {
                    reject(
                            SLOT_NOT_FOUND,
                            "Không tìm thấy khung giờ."
                    );
                }

                if (!"Available".equals(
                        slot.getSlotStatus()
                )) {
                    reject(
                            SLOT_UNAVAILABLE,
                            "Khung giờ vừa được đặt hoặc không còn trống."
                    );
                }

                if (!slot.isCourtActive()
                        || !slot.isCourtTypeActive()
                        || !"Available".equals(
                                slot.getCourtStatus()
                        )
                        || slot.getCourtPrice() == null
                        || slot.getCourtPrice().signum() < 0) {

                    reject(
                            COURT_UNAVAILABLE,
                            "Sân hiện không thể nhận booking."
                    );
                }

                if (slot.getSlotDate() == null
                        || slot.getStartTime() == null) {
                    reject(
                            SLOT_UNAVAILABLE,
                            "Thông tin khung giờ không hợp lệ."
                    );
                }

                LocalDateTime slotStart
                        = LocalDateTime.of(
                                slot.getSlotDate(),
                                slot.getStartTime()
                        );

                if (!slotStart.isAfter(
                        LocalDateTime.now(clock)
                )) {
                    reject(
                            SLOT_EXPIRED,
                            "Khung giờ đã bắt đầu hoặc đã kết thúc."
                    );
                }

                List<model.BookingService> resolvedLines
                        = new ArrayList<>();

                BigDecimal serviceTotal
                        = BigDecimal.ZERO;

                for (Map.Entry<Integer, Integer> entry
                        : normalizedServices.entrySet()) {

                    int serviceID = entry.getKey();
                    int quantity = entry.getValue();

                    Service selectedService
                            = serviceDAO.findActiveByID(
                                    connection,
                                    serviceID
                            );

                    if (selectedService == null
                            || !selectedService.isActive()
                            || selectedService.getPrice() == null
                            || selectedService.getPrice()
                                    .signum() < 0) {

                        reject(
                                SERVICE_UNAVAILABLE,
                                "Một dịch vụ đã bị ẩn hoặc không còn tồn tại."
                        );
                    }

                    BigDecimal lineTotal
                            = selectedService.getPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    quantity
                                            )
                                    );

                    serviceTotal = serviceTotal.add(
                            lineTotal
                    );

                    model.BookingService serviceLine
                            = new model.BookingService();

                    serviceLine.setServiceID(serviceID);
                    serviceLine.setQuantity(quantity);
                    serviceLine.setPriceAtOrder(
                            selectedService.getPrice()
                    );

                    resolvedLines.add(serviceLine);
                }

                BigDecimal totalAmount
                        = slot.getCourtPrice()
                                .add(serviceTotal);

                Booking booking = new Booking();

                booking.setAccountID(accountID);
                booking.setTimeSlotID(timeSlotID);
                booking.setCourtPriceAtOrder(
                        slot.getCourtPrice()
                );
                booking.setServiceTotalAtOrder(
                        serviceTotal
                );
                booking.setTotalAmount(totalAmount);
                booking.setStatus("Pending");
                booking.setNotes(normalizedNotes);

                /*
                 * Rule 1: Tao Booking khong tao Invoice.
                 * Rule 6: Khong nhan CourtID tu client.
                 * Rule 7: Snapshot gia court va service.
                 */
                int bookingID = bookingDAO.insertBooking(
                        connection,
                        booking
                );

                for (model.BookingService serviceLine
                        : resolvedLines) {

                    serviceLine.setBookingID(bookingID);

                    bookingDAO.insertBookingService(
                            connection,
                            serviceLine
                    );
                }

                boolean slotUpdated
                        = bookingDAO.markSlotBooked(
                                connection,
                                timeSlotID
                        );

                if (!slotUpdated) {
                    reject(
                            SLOT_UNAVAILABLE,
                            "Khung giờ vừa được đặt, vui lòng chọn khung giờ khác."
                    );
                }

                connection.commit();

                return CreateBookingResult.success(
                        bookingID
                );
            } catch (BookingRuleException exception) {
                rollback(connection, exception);

                return CreateBookingResult.failure(
                        exception.getErrorCode(),
                        exception.getMessage()
                );
            } catch (SQLException exception) {
                rollback(connection, exception);

                /*
                 * Rule 8: unique index la lop bao ve cuoi
                 * cung cho double-booking.
                 */
                if (bookingDAO.isActiveSlotConflict(
                        exception
                )) {
                    return CreateBookingResult.failure(
                            SLOT_UNAVAILABLE,
                            "Khung giờ vừa được đặt, vui lòng chọn khung giờ khác."
                    );
                }

                throw exception;
            } catch (RuntimeException | Error exception) {
                rollback(connection, exception);
                throw exception;
            }
        }
    }

    /**
     * U11: danh sach booking cua account dang dang nhap.
     */
    public List<Booking> listMine(
            int accountID
    ) throws SQLException {
        requirePositiveID(accountID, "AccountID");

        return bookingDAO.findByAccountID(
                accountID
        );
    }

    /**
     * U12: booking detail + service lines, co ownership.
     */
    public BookingDetail getMine(
            int bookingID,
            int accountID
    ) throws SQLException {
        requirePositiveID(bookingID, "BookingID");
        requirePositiveID(accountID, "AccountID");

        Booking booking
                = bookingDAO.findDetailByIDAndAccountID(
                        bookingID,
                        accountID
                );

        if (booking == null) {
            return null;
        }

        List<model.BookingService> serviceLines
                = bookingDAO
                        .findServicesByBookingIDAndAccountID(
                                bookingID,
                                accountID
                        );

        return new BookingDetail(
                booking,
                serviceLines
        );
    }

    private static String normalizeNotes(
            String notes
    ) {
        if (notes == null) {
            return null;
        }

        String normalized = notes.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > MAX_NOTES_LENGTH) {
            reject(
                    INVALID_INPUT,
                    "Ghi chú tối đa 255 ký tự."
            );
        }

        return normalized;
    }

    private static Map<Integer, Integer>
            normalizeServices(
                    Map<Integer, Integer> source
            ) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        if (source.size() > 20) {
            reject(
                    INVALID_INPUT,
                    "Số lượng loại dịch vụ không hợp lệ."
            );
        }

        LinkedHashMap<Integer, Integer> normalized
                = new LinkedHashMap<>();

        for (Map.Entry<Integer, Integer> entry
                : source.entrySet()) {

            Integer serviceID = entry.getKey();
            Integer quantity = entry.getValue();

            if (serviceID == null
                    || serviceID <= 0
                    || quantity == null
                    || quantity <= 0
                    || quantity > MAX_SERVICE_QUANTITY) {

                reject(
                        INVALID_INPUT,
                        "Số lượng dịch vụ không hợp lệ."
                );
            }

            normalized.put(
                    serviceID,
                    quantity
            );
        }

        return normalized;
    }

    private static void requirePositiveID(
            int value,
            String name
    ) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    name + " must be positive"
            );
        }
    }

    private static void reject(
            String errorCode,
            String message
    ) {
        throw new BookingRuleException(
                errorCode,
                message
        );
    }

    private static void rollback(
            Connection connection,
            Throwable originalFailure
    ) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            originalFailure.addSuppressed(
                    rollbackFailure
            );
        }
    }

    private static final class BookingRuleException
            extends RuntimeException {

        private final String errorCode;

        private BookingRuleException(
                String errorCode,
                String message
        ) {
            super(message);
            this.errorCode = errorCode;
        }

        private String getErrorCode() {
            return errorCode;
        }
    }

    public static final class CreateBookingResult {

        private final boolean success;
        private final int bookingID;
        private final String errorCode;
        private final String errorMessage;

        private CreateBookingResult(
                boolean success,
                int bookingID,
                String errorCode,
                String errorMessage
        ) {
            this.success = success;
            this.bookingID = bookingID;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        private static CreateBookingResult success(
                int bookingID
        ) {
            return new CreateBookingResult(
                    true,
                    bookingID,
                    null,
                    null
            );
        }

        private static CreateBookingResult failure(
                String errorCode,
                String errorMessage
        ) {
            return new CreateBookingResult(
                    false,
                    0,
                    errorCode,
                    errorMessage
            );
        }

        public boolean isSuccess() {
            return success;
        }

        public int getBookingID() {
            return bookingID;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static final class BookingDetail {

        private final Booking booking;
        private final List<model.BookingService> serviceLines;

        private BookingDetail(
                Booking booking,
                List<model.BookingService> serviceLines
        ) {
            this.booking = booking;
            this.serviceLines
                    = Collections.unmodifiableList(
                            new ArrayList<>(
                                    serviceLines
                            )
                    );
        }

        public Booking getBooking() {
            return booking;
        }

        public List<model.BookingService>
                getServiceLines() {
            return serviceLines;
        }
    }
}