package service;

import dao.AdminBookingDAO;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Booking;
import utils.AppConstants;

public class AdminBookingService {

    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INVALID_STATUS = "INVALID_STATUS";
    public static final String CHECK_IN_WINDOW_CLOSED
            = "CHECK_IN_WINDOW_CLOSED";
    public static final String CHECK_IN_CONFLICT = "CHECK_IN_CONFLICT";
    private static final Set<String> FILTERS = new HashSet<>(Arrays.asList(
            AppConstants.BOOKING_PENDING,
            AppConstants.BOOKING_CONFIRMED,
            AppConstants.BOOKING_CHECKED_IN,
            AppConstants.BOOKING_COMPLETED,
            AppConstants.BOOKING_CANCELLED,
            AppConstants.BOOKING_NO_SHOW));
    private final AdminBookingDAO dao;
    private final Clock clock;

    public AdminBookingService() {
        this(new AdminBookingDAO(), Clock.systemDefaultZone());
    }
    public AdminBookingService(AdminBookingDAO dao, Clock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public int reconcile() throws SQLException {
        return dao.reconcile(LocalDateTime.now(clock));
    }

    public List<Booking> list(String status) throws SQLException {
        reconcile();
        String normalized = normalizeStatus(status);
        return dao.findAll(normalized);
    }

    public BookingDetail getDetail(int bookingID) throws SQLException {
        if (bookingID <= 0) return null;
        reconcile();
        Booking booking = dao.findDetail(bookingID);
        return booking == null ? null
                : new BookingDetail(booking, dao.findServices(bookingID));
    }

    public ActionResult checkIn(int bookingID) throws SQLException {
        if (bookingID <= 0) return ActionResult.failure(NOT_FOUND,
                "Không tìm thấy booking.");
        reconcile();
        Booking booking = dao.findDetail(bookingID);
        if (booking == null) return ActionResult.failure(NOT_FOUND,
                "Không tìm thấy booking.");
        LocalDateTime now = LocalDateTime.now(clock);
        if (!canCheckIn(booking, now)) {
            return ActionResult.failure(CHECK_IN_WINDOW_CLOSED,
                    "Booking không nằm trong cửa sổ check-in.");
        }
        LocalDateTime start = LocalDateTime.of(
                booking.getSlotDate(), booking.getStartTime());
        boolean updated = dao.checkIn(bookingID, start.minusMinutes(30),
                start.plusMinutes(30), now);
        return updated ? ActionResult.success("Check-in thành công.")
                : ActionResult.failure(CHECK_IN_CONFLICT,
                        "Booking vừa được cập nhật, vui lòng tải lại.");
    }

    public boolean canCheckIn(Booking booking, LocalDateTime now) {
        if (booking == null || now == null
                || !AppConstants.BOOKING_CONFIRMED.equals(booking.getStatus())
                || booking.getSlotDate() == null
                || booking.getStartTime() == null) return false;
        LocalDateTime start = LocalDateTime.of(
                booking.getSlotDate(), booking.getStartTime());
        return !now.isBefore(start.minusMinutes(30))
                && now.isBefore(start.plusMinutes(30));
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()
                || "all".equalsIgnoreCase(status.trim())) return null;
        String normalized = status.trim();
        return FILTERS.contains(normalized) ? normalized : null;
    }

    public static final class BookingDetail {
        private final Booking booking;
        private final List<model.BookingService> serviceLines;
        public BookingDetail(Booking booking,
                List<model.BookingService> serviceLines) {
            this.booking = booking;
            this.serviceLines = serviceLines;
        }
        public Booking getBooking() { return booking; }
        public List<model.BookingService> getServiceLines() {
            return serviceLines;
        }
    }

    public static final class ActionResult {
        private final boolean success;
        private final String errorCode;
        private final String message;
        private ActionResult(boolean success, String code, String message) {
            this.success = success;
            this.errorCode = code;
            this.message = message;
        }
        public static ActionResult success(String message) {
            return new ActionResult(true, null, message);
        }
        public static ActionResult failure(String code, String message) {
            return new ActionResult(false, code, message);
        }
        public boolean isSuccess() { return success; }
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
    }
}
