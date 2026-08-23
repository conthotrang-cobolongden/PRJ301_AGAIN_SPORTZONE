package service;

import dao.AdminBookingDAO;
import dao.AdminCourtDAO;
import dao.TimeSlotDAO;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Booking;

public final class AdminMvpDomainTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-08-24T03:00:00Z"), ZONE);

    private AdminMvpDomainTest() { }

    public static void main(String[] args) throws Exception {
        shouldValidateCourtUpdateAndSurfaceBlockingCount();
        shouldValidateMondayAndDelegateWeeklyGeneration();
        shouldAllowCheckInOnlyInsideWindow();
        shouldReconcileBeforeReadsAndContainTerminalSql();
        System.out.println("AdminMvpDomainTest PASSED");
    }

    private static void shouldValidateCourtUpdateAndSurfaceBlockingCount()
            throws Exception {
        FakeAdminCourtDAO dao = new FakeAdminCourtDAO();
        AdminCourtService service = new AdminCourtService(dao);

        AdminCourtService.UpdateResult invalid = service.update(
                1, "-1", "Available", LocalDateTime.now(FIXED_CLOCK));
        require(!invalid.isSuccess()
                && AdminCourtService.INVALID_PRICE.equals(invalid.getErrorCode())
                && dao.updateCalls == 0,
                "Negative price must be rejected before DAO");

        dao.outcome = new AdminCourtDAO.CourtUpdateOutcome(true, false, 2);
        AdminCourtService.UpdateResult blocked = service.update(
                1, "150000", "Maintenance", LocalDateTime.now(FIXED_CLOCK));
        require(!blocked.isSuccess()
                && AdminCourtService.COURT_HAS_FUTURE_BOOKINGS.equals(
                        blocked.getErrorCode())
                && blocked.getBlockingCount() == 2,
                "Blocking future bookings must reject unavailable status");

        dao.outcome = new AdminCourtDAO.CourtUpdateOutcome(true, true, 0);
        AdminCourtService.UpdateResult updated = service.update(
                1, "175000", "Unavailable", LocalDateTime.now(FIXED_CLOCK));
        require(updated.isSuccess() && dao.updateCalls == 2,
                "Valid court update must be delegated once");
    }

    private static void shouldValidateMondayAndDelegateWeeklyGeneration()
            throws Exception {
        FakeTimeSlotDAO dao = new FakeTimeSlotDAO();
        WeeklySlotService service = new WeeklySlotService(dao, FIXED_CLOCK);

        WeeklySlotService.GenerateResult invalid = service.generate(
                LocalDate.of(2026, 8, 25));
        require(!invalid.isSuccess()
                && WeeklySlotService.INVALID_WEEK.equals(invalid.getErrorCode())
                && dao.calls == 0,
                "Non-Monday must be rejected");

        WeeklySlotService.GenerateResult valid = service.generate(
                LocalDate.of(2026, 8, 31));
        require(valid.isSuccess() && valid.getCreated() == 520
                && valid.getSkipped() == 40 && dao.calls == 1,
                "Valid week must expose created and skipped totals");
    }

    private static void shouldAllowCheckInOnlyInsideWindow()
            throws Exception {
        FakeAdminBookingDAO dao = new FakeAdminBookingDAO();
        dao.detail = confirmedBooking();

        AdminBookingService service = new AdminBookingService(dao, FIXED_CLOCK);
        require(service.canCheckIn(dao.detail, LocalDateTime.now(FIXED_CLOCK)),
                "Confirmed booking at start time must allow check-in");
        AdminBookingService.ActionResult success = service.checkIn(10);
        require(success.isSuccess() && dao.checkInCalls == 1,
                "Check-in inside window must use conditional DAO update");

        Clock earlyClock = Clock.fixed(
                Instant.parse("2026-08-24T02:29:00Z"), ZONE);
        AdminBookingService early = new AdminBookingService(dao, earlyClock);
        require(AdminBookingService.CHECK_IN_WINDOW_CLOSED.equals(
                early.checkIn(10).getErrorCode()),
                "Check-in earlier than 30 minutes must fail");

        Clock deadlineClock = Clock.fixed(
                Instant.parse("2026-08-24T03:30:00Z"), ZONE);
        AdminBookingService deadline = new AdminBookingService(
                dao, deadlineClock);
        require(AdminBookingService.CHECK_IN_WINDOW_CLOSED.equals(
                deadline.checkIn(10).getErrorCode()),
                "Check-in at start plus 30 minutes belongs to NoShow");
    }

    private static void shouldReconcileBeforeReadsAndContainTerminalSql()
            throws Exception {
        FakeAdminBookingDAO dao = new FakeAdminBookingDAO();
        dao.detail = confirmedBooking();
        AdminBookingService service = new AdminBookingService(dao, FIXED_CLOCK);

        service.list(null);
        service.getDetail(10);
        require(dao.reconcileCalls == 2,
                "Admin list and detail must reconcile before reading");

        String source = new String(Files.readAllBytes(Paths.get(
                "src", "java", "dao", "AdminBookingDAO.java")),
                StandardCharsets.UTF_8);
        require(source.contains("Status = N'NoShow'")
                && source.contains("Status = N'Completed'")
                && source.contains("Status = N'CheckedIn'"),
                "Persistence must contain NoShow, Completed and CheckedIn SQL");
    }

    private static Booking confirmedBooking() {
        Booking booking = new Booking();
        booking.setBookingID(10);
        booking.setStatus("Confirmed");
        booking.setSlotDate(LocalDate.of(2026, 8, 24));
        booking.setStartTime(LocalTime.of(10, 0));
        booking.setEndTime(LocalTime.of(11, 0));
        return booking;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static final class FakeAdminCourtDAO extends AdminCourtDAO {
        private int updateCalls;
        private CourtUpdateOutcome outcome
                = new CourtUpdateOutcome(true, true, 0);

        @Override
        public CourtUpdateOutcome updatePriceAndStatus(int courtID,
                BigDecimal price, String status, LocalDateTime now) {
            updateCalls++;
            return outcome;
        }
    }

    private static final class FakeTimeSlotDAO extends TimeSlotDAO {
        private int calls;

        @Override
        public GenerationResult generateWeek(LocalDate monday) {
            calls++;
            return new GenerationResult(520, 40);
        }
    }

    private static final class FakeAdminBookingDAO extends AdminBookingDAO {
        private int reconcileCalls;
        private int checkInCalls;
        private Booking detail;

        @Override
        public int reconcile(LocalDateTime now) {
            reconcileCalls++;
            return 0;
        }

        @Override
        public List<Booking> findAll(String status) {
            return detail == null
                    ? Collections.emptyList()
                    : Collections.singletonList(detail);
        }

        @Override
        public Booking findDetail(int bookingID) {
            return detail;
        }

        @Override
        public List<model.BookingService> findServices(int bookingID) {
            return new ArrayList<>();
        }

        @Override
        public boolean checkIn(int bookingID, LocalDateTime earliest,
                LocalDateTime deadline, LocalDateTime now) {
            checkInCalls++;
            return true;
        }
    }
}
