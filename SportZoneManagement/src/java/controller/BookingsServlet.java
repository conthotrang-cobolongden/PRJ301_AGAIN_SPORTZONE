package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Account;
import model.Booking;
import service.BookingService;
import utils.AppConstants;
import utils.SessionUtil;
import utils.Views;

/**
 * GET action bookings.
 * Tag: U11.
 */
public class BookingsServlet extends HttpServlet {

    private final BookingService bookingService;
    private final Clock clock;

    public BookingsServlet() {
        this(
                new BookingService(),
                Clock.systemDefaultZone()
        );
    }

    BookingsServlet(
            BookingService bookingService,
            Clock clock
    ) {
        if (bookingService == null || clock == null) {
            throw new IllegalArgumentException(
                    "Bookings dependencies must not be null"
            );
        }

        this.bookingService = bookingService;
        this.clock = clock;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        Account account = requireUser(
                request,
                response
        );

        if (account == null) {
            return;
        }

        List<Booking> allBookings;

        try {
            allBookings = bookingService.listMine(
                    account.getAccountID()
            );
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load bookings",
                    exception
            );
        }

        LocalDateTime now = LocalDateTime.now(clock);

        List<Booking> upcomingList
                = new ArrayList<>();

        List<Booking> historyList
                = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (isUpcoming(booking, now)) {
                upcomingList.add(booking);
            } else {
                historyList.add(booking);
            }
        }

        upcomingList.sort(
                Comparator
                        .comparing(Booking::getSlotDate)
                        .thenComparing(
                                Booking::getStartTime
                        )
        );

        String activeTab = "history".equals(
                request.getParameter("tab")
        ) ? "history" : "upcoming";

        request.setAttribute(
                "upcomingList",
                upcomingList
        );
        request.setAttribute(
                "historyList",
                historyList
        );
        request.setAttribute(
                "activeTab",
                activeTab
        );

        request.getRequestDispatcher(
                Views.BOOKING_LIST
        ).forward(request, response);
    }

    private boolean isUpcoming(
            Booking booking,
            LocalDateTime now
    ) {
        if (!"Pending".equals(booking.getStatus())
                && !"Confirmed".equals(
                        booking.getStatus()
                )) {
            return false;
        }

        if (booking.getSlotDate() == null
                || booking.getStartTime() == null) {
            return false;
        }

        LocalDateTime slotStart = LocalDateTime.of(
                booking.getSlotDate(),
                booking.getStartTime()
        );

        return slotStart.isAfter(now);
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
}
