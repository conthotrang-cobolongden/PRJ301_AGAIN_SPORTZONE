package controller;

import dao.CourtDAO;
import dao.ServiceDAO;
import dao.TimeSlotDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Account;
import model.Court;
import model.TimeSlot;
import service.BookingService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.DateUtil;
import utils.SessionUtil;
import utils.Views;

/**
 * U10: GET/POST /booking/create.
 */
public class BookingCreateServlet extends HttpServlet {

    private static final int SCHEDULE_DAY_COUNT = 7;

    private final CourtDAO courtDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final ServiceDAO serviceDAO;
    private final BookingService bookingService;
    private final Clock clock;

    public BookingCreateServlet() {
        this(
                new CourtDAO(),
                new TimeSlotDAO(),
                new ServiceDAO(),
                new BookingService(),
                Clock.systemDefaultZone()
        );
    }

    BookingCreateServlet(
            CourtDAO courtDAO,
            TimeSlotDAO timeSlotDAO,
            ServiceDAO serviceDAO,
            BookingService bookingService,
            Clock clock
    ) {
        this.courtDAO = courtDAO;
        this.timeSlotDAO = timeSlotDAO;
        this.serviceDAO = serviceDAO;
        this.bookingService = bookingService;
        this.clock = clock;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        Account account = requireUser(request, response);

        if (account == null) {
            return;
        }

        CsrfUtil.ensureToken(request.getSession());

        renderForm(
                request,
                response,
                parsePositiveInteger(
                        request.getParameter("courtId")
                ),
                request.getParameter("date"),
                parsePositiveInteger(
                        request.getParameter("slotId")
                ),
                Collections.emptyMap(),
                null
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }

        Account account = requireUser(request, response);

        if (account == null) {
            return;
        }

        if (SessionUtil.isFrozen(
                request.getSession(false)
        )) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }

        Integer courtID = parsePositiveInteger(
                request.getParameter("courtId")
        );

        Integer timeSlotID = parsePositiveInteger(
                request.getParameter("slotId")
        );

        String selectedDate
                = request.getParameter("date");

        if (timeSlotID == null) {
            renderForm(
                    request,
                    response,
                    courtID,
                    selectedDate,
                    null,
                    Collections.emptyMap(),
                    "Vui lòng chọn một khung giờ trống."
            );
            return;
        }

        Map<Integer, Integer> serviceQuantities;

        try {
            serviceQuantities
                    = parseServiceQuantities(request);
        } catch (IllegalArgumentException exception) {
            renderForm(
                    request,
                    response,
                    courtID,
                    selectedDate,
                    timeSlotID,
                    Collections.emptyMap(),
                    exception.getMessage()
            );
            return;
        }

        BookingService.CreateBookingResult result;

        try {
            /*
             * Rule 6:
             * courtId va date chi dung render lai form.
             * Persistence chi nhan TimeSlotID.
             */
            result = bookingService.create(
                    account.getAccountID(),
                    timeSlotID,
                    serviceQuantities,
                    request.getParameter("notes")
            );
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to create booking",
                    exception
            );
        }

        if (!result.isSuccess()) {
            renderForm(
                    request,
                    response,
                    courtID,
                    selectedDate,
                    timeSlotID,
                    serviceQuantities,
                    result.getErrorMessage()
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/booking?id="
                + result.getBookingID()
                + "&created=1"
        );
    }

    private void renderForm(
            HttpServletRequest request,
            HttpServletResponse response,
            Integer requestedCourtID,
            String rawDate,
            Integer requestedSlotID,
            Map<Integer, Integer> selectedServices,
            String error
    ) throws ServletException, IOException {
        LocalDate today = LocalDate.now(clock);
        LocalDate maxDate = today.plusDays(
                SCHEDULE_DAY_COUNT - 1L
        );
        LocalDate selectedDate = resolveDate(
                rawDate,
                today,
                maxDate
        );

        try {
            List<Court> availableCourts
                    = filterAvailableCourts(
                            courtDAO.findAll(null)
                    );

            Court selectedCourt = findSelectedCourt(
                    availableCourts,
                    requestedCourtID
            );

            List<TimeSlot> slots = new ArrayList<>();

            if (selectedCourt != null) {
                slots = timeSlotDAO.listByCourtAndDate(
                        selectedCourt.getCourtID(),
                        selectedDate
                );

                markPastSlots(
                        slots,
                        LocalDateTime.now(clock)
                );
            }

            request.setAttribute(
                    "courtList",
                    availableCourts
            );
            request.setAttribute(
                    "selectedCourtId",
                    selectedCourt == null
                            ? 0
                            : selectedCourt.getCourtID()
            );
            request.setAttribute(
                    "selectedCourtPrice",
                    selectedCourt == null
                            ? 0
                            : selectedCourt.getPricePerHour()
            );
            request.setAttribute(
                    "selectedDate",
                    selectedDate.toString()
            );
            request.setAttribute(
                    "minDate",
                    today.toString()
            );
            request.setAttribute(
                    "maxDate",
                    maxDate.toString()
            );
            request.setAttribute(
                    "selectedSlotId",
                    requestedSlotID == null
                            ? 0
                            : requestedSlotID
            );
            request.setAttribute(
                    "slotList",
                    slots
            );
            request.setAttribute(
                    "serviceList",
                    serviceDAO.findAllActive()
            );
            request.setAttribute(
                    "selectedServiceQuantities",
                    selectedServices == null
                            ? Collections.emptyMap()
                            : selectedServices
            );

            if (error != null) {
                request.setAttribute("error", error);
            }

            request.getRequestDispatcher(
                    Views.BOOKING_CREATE
            ).forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load booking form",
                    exception
            );
        }
    }

    private Account requireUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        Account account = SessionUtil.getLoggedInAccount(
                request.getSession(false)
        );

        if (account == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return null;
        }

        if (!AppConstants.ROLE_USER.equals(
                account.getRoleName()
        )) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return null;
        }

        return account;
    }

    private List<Court> filterAvailableCourts(
            List<Court> courts
    ) {
        List<Court> available = new ArrayList<>();

        for (Court court : courts) {
            if (court.isActive()
                    && "Available".equals(
                            court.getStatus()
                    )) {
                available.add(court);
            }
        }

        return available;
    }

    private Court findSelectedCourt(
            List<Court> courts,
            Integer requestedCourtID
    ) {
        if (requestedCourtID != null) {
            for (Court court : courts) {
                if (court.getCourtID()
                        == requestedCourtID) {
                    return court;
                }
            }
        }

        return courts.isEmpty()
                ? null
                : courts.get(0);
    }

    private Map<Integer, Integer>
            parseServiceQuantities(
                    HttpServletRequest request
            ) {
        String[] rawServiceIDs
                = request.getParameterValues(
                        "serviceId"
                );

        if (rawServiceIDs == null
                || rawServiceIDs.length == 0) {
            return Collections.emptyMap();
        }

        if (rawServiceIDs.length > 20) {
            throw new IllegalArgumentException(
                    "Số lượng loại dịch vụ không hợp lệ."
            );
        }

        LinkedHashMap<Integer, Integer> result
                = new LinkedHashMap<>();

        for (String rawServiceID : rawServiceIDs) {
            Integer serviceID
                    = parsePositiveInteger(rawServiceID);

            if (serviceID == null
                    || result.containsKey(serviceID)) {
                throw new IllegalArgumentException(
                        "Thông tin dịch vụ không hợp lệ."
                );
            }

            Integer quantity = parsePositiveInteger(
                    request.getParameter(
                            "qty_" + serviceID
                    )
            );

            if (quantity == null || quantity > 20) {
                throw new IllegalArgumentException(
                        "Số lượng dịch vụ phải từ 1 đến 20."
                );
            }

            result.put(serviceID, quantity);
        }

        return result;
    }

    private LocalDate resolveDate(
            String rawDate,
            LocalDate minDate,
            LocalDate maxDate
    ) {
        if (rawDate == null
                || rawDate.trim().isEmpty()) {
            return minDate;
        }

        try {
            LocalDate parsed = LocalDate.parse(
                    rawDate.trim()
            );

            if (parsed.isBefore(minDate)) {
                return minDate;
            }

            if (parsed.isAfter(maxDate)) {
                return maxDate;
            }

            return parsed;
        } catch (DateTimeParseException exception) {
            return minDate;
        }
    }

    private void markPastSlots(
            List<TimeSlot> slots,
            LocalDateTime now
    ) {
        for (TimeSlot slot : slots) {
            slot.setPast(
                    DateUtil.hasStarted(
                            slot.getSlotDate(),
                            slot.getStartTime(),
                            now
                    )
            );
        }
    }

    private Integer parsePositiveInteger(
            String rawValue
    ) {
        if (rawValue == null
                || rawValue.trim().isEmpty()) {
            return null;
        }

        try {
            int value = Integer.parseInt(
                    rawValue.trim()
            );

            return value > 0 ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
