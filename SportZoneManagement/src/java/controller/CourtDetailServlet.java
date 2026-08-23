package controller;

import dao.CourtDAO;
import dao.TimeSlotDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Court;
import model.DateOption;
import model.TimeSlot;
import utils.DateUtil;
import utils.Views;

/**
 * Hien thi chi tiet san va lich slot theo ngay.
 *
 * Route:
 * GET /court?id=<courtID>&date=<yyyy-MM-dd>
 *
 * Request attributes:
 * - court
 * - slotList
 * - selectedDate
 * - selectedDateObj
 * - dateOptions
 *
 * Rule:
 * - Court ID khong hop le hoac san inactive: HTTP 404.
 * - Ngay duoc gioi han tu hom nay den hom nay + 6.
 * - DAO tra du Available, Booked va Closed.
 * - Servlet danh dau slot da bat dau bang thuoc tinh past.
 *
 * Tag: U8, U9
 */
public class CourtDetailServlet extends HttpServlet {

    private static final int SCHEDULE_DAY_COUNT = 7;

    private static final DateTimeFormatter DAY_MONTH_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM");

    private final CourtDAO courtDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final Clock clock;

    /**
     * Constructor Tomcat dung khi khoi tao servlet tu web.xml.
     */
    public CourtDetailServlet() {
        this(
                new CourtDAO(),
                new TimeSlotDAO(),
                Clock.systemDefaultZone()
        );
    }

    /**
     * Constructor dependency injection cho unit test.
     *
     * Clock duoc inject de test khong phu thuoc ngay/gio that.
     */
    CourtDetailServlet(
            CourtDAO courtDAO,
            TimeSlotDAO timeSlotDAO,
            Clock clock
    ) {
        if (courtDAO == null) {
            throw new IllegalArgumentException(
                    "CourtDAO must not be null"
            );
        }

        if (timeSlotDAO == null) {
            throw new IllegalArgumentException(
                    "TimeSlotDAO must not be null"
            );
        }

        if (clock == null) {
            throw new IllegalArgumentException(
                    "Clock must not be null"
            );
        }

        this.courtDAO = courtDAO;
        this.timeSlotDAO = timeSlotDAO;
        this.clock = clock;
    }

    /**
     * Xu ly GET /court.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        Integer requestedCourtID = parsePositiveInteger(
                request.getParameter("id")
        );

        if (requestedCourtID == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate lastAllowedDate = today.plusDays(
                SCHEDULE_DAY_COUNT - 1L
        );

        LocalDate selectedDate = resolveSelectedDate(
                request.getParameter("date"),
                today,
                lastAllowedDate
        );

        Court court;
        List<TimeSlot> slotList;

        try {
            court = courtDAO.findById(
                    requestedCourtID
            );

            /*
             * CourtDAO tra null khi:
             * - ID khong ton tai.
             * - Courts.IsActive = 0.
             * - CourtTypes.IsActive = 0.
             */
            if (court == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            /*
             * Rule 6:
             * Sau khi da load Court, dung CourtID cua object tin cay,
             * khong tiep tuc tin raw request parameter.
             */
            slotList = timeSlotDAO.listByCourtAndDate(
                    court.getCourtID(),
                    selectedDate
            );
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load court schedule",
                    exception
            );
        }

        markPastSlots(
                slotList,
                LocalDateTime.now(clock)
        );

        List<DateOption> dateOptions
                = createDateOptions(
                        today,
                        selectedDate
                );

        request.setAttribute(
                "court",
                court
        );

        request.setAttribute(
                "slotList",
                slotList
        );

        request.setAttribute(
                "selectedDate",
                selectedDate.toString()
        );

        /*
         * court/detail.jsp dung fmt:formatDate cho attribute nay.
         * JSTL fmt:formatDate chap nhan java.util.Date/java.sql.Date,
         * khong nhan truc tiep java.time.LocalDate.
         */
        request.setAttribute(
                "selectedDateObj",
                DateUtil.toSqlDate(selectedDate)
        );

        request.setAttribute(
                "dateOptions",
                dateOptions
        );

        request.getRequestDispatcher(
                Views.COURT_DETAIL
        ).forward(request, response);
    }

    /**
     * Parse mot ID nguyen duong.
     *
     * Tra null thay vi nem NumberFormatException de servlet co the
     * phan hoi HTTP 404 cho URL khong hop le.
     */
    private Integer parsePositiveInteger(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String normalized = rawValue.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        try {
            int value = Integer.parseInt(normalized);

            return value > 0
                    ? Integer.valueOf(value)
                    : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Parse va clamp ngay vao khoang [today, today + 6].
     *
     * - Null/rong/sai format: today.
     * - Truoc today: today.
     * - Sau today + 6: today + 6.
     */
    private LocalDate resolveSelectedDate(
            String rawDate,
            LocalDate today,
            LocalDate lastAllowedDate
    ) {
        if (rawDate == null) {
            return today;
        }

        String normalized = rawDate.trim();

        if (normalized.isEmpty()) {
            return today;
        }

        LocalDate parsedDate;

        try {
            parsedDate = LocalDate.parse(normalized);
        } catch (DateTimeParseException exception) {
            return today;
        }

        if (parsedDate.isBefore(today)) {
            return today;
        }

        if (parsedDate.isAfter(lastAllowedDate)) {
            return lastAllowedDate;
        }

        return parsedDate;
    }

    /**
     * Danh dau slot da bat dau.
     *
     * DateUtil.hasStarted dung phep so sanh inclusive:
     * slot bat dau dung thoi diem hien tai cung duoc xem la da qua,
     * vi user khong con du thoi gian dat slot do.
     */
    private void markPastSlots(
            List<TimeSlot> slots,
            LocalDateTime now
    ) {
        for (TimeSlot slot : slots) {
            boolean past = DateUtil.hasStarted(
                    slot.getSlotDate(),
                    slot.getStartTime(),
                    now
            );

            slot.setPast(past);
        }
    }

    /**
     * Tao day 7 ngay bat dau tu today.
     */
    private List<DateOption> createDateOptions(
            LocalDate today,
            LocalDate selectedDate
    ) {
        List<DateOption> options
                = new ArrayList<>(SCHEDULE_DAY_COUNT);

        for (int offset = 0;
                offset < SCHEDULE_DAY_COUNT;
                offset++) {
            LocalDate date = today.plusDays(offset);

            options.add(
                    new DateOption(
                            date.toString(),
                            vietnameseWeekday(date),
                            date.format(DAY_MONTH_FORMAT),
                            date.equals(selectedDate)
                    )
            );
        }

        return options;
    }

    /**
     * Chuyen DayOfWeek thanh nhan tieng Viet ngan gon.
     */
    private String vietnameseWeekday(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "T2";

            case TUESDAY:
                return "T3";

            case WEDNESDAY:
                return "T4";

            case THURSDAY:
                return "T5";

            case FRIDAY:
                return "T6";

            case SATURDAY:
                return "T7";

            case SUNDAY:
                return "CN";

            default:
                throw new IllegalStateException(
                        "Unsupported day of week: "
                        + date.getDayOfWeek()
                );
        }
    }
}