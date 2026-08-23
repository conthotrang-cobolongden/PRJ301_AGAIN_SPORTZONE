package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Account;
import service.BookingService;
import utils.AppConstants;
import utils.SessionUtil;
import utils.Views;

/**
 * GET /booking?id=...
 * Tag: U12.
 */
public class BookingDetailServlet extends HttpServlet {

    private final BookingService bookingService;

    public BookingDetailServlet() {
        this(new BookingService());
    }

    BookingDetailServlet(
            BookingService bookingService
    ) {
        if (bookingService == null) {
            throw new IllegalArgumentException(
                    "BookingService must not be null"
            );
        }

        this.bookingService = bookingService;
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

        Integer bookingID = parsePositiveInteger(
                request.getParameter("id")
        );

        if (bookingID == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        BookingService.BookingDetail detail;

        try {
            /*
             * U12 ownership:
             * BookingID va AccountID cung duoc truyen vao service.
             */
            detail = bookingService.getMine(
                    bookingID,
                    account.getAccountID()
            );
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load booking detail",
                    exception
            );
        }

        if (detail == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        request.setAttribute(
                "booking",
                detail.getBooking()
        );
        request.setAttribute(
                "serviceLines",
                detail.getServiceLines()
        );

        if ("1".equals(
                request.getParameter("created")
        )) {
            request.setAttribute(
                    "success",
                    "Đặt sân thành công. Booking đang chờ xác nhận."
            );
        }

        request.getRequestDispatcher(
                Views.BOOKING_DETAIL
        ).forward(request, response);
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