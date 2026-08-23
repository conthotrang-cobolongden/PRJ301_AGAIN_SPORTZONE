package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import service.AdminBookingService;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

public class AdminBookingsServlet extends HttpServlet {

    private final AdminBookingService service = new AdminBookingService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;
        CsrfUtil.ensureToken(request.getSession());
        consumeFlash(request);
        Integer bookingID = positiveID(request.getParameter("id"));
        try {
            if (bookingID != null) {
                AdminBookingService.BookingDetail detail
                        = service.getDetail(bookingID);
                if (detail == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("booking", detail.getBooking());
                request.setAttribute("serviceLines", detail.getServiceLines());
                request.setAttribute("canCheckIn", service.canCheckIn(
                        detail.getBooking(), LocalDateTime.now()));
                if ("1".equals(request.getParameter("created"))) {
                    request.setAttribute("success",
                            "Đã tạo booking cho Khách vãng lai.");
                }
            } else {
                String status = request.getParameter("status");
                request.setAttribute("selectedStatus", status);
                request.setAttribute("bookingList", service.list(status));
            }
            request.getRequestDispatcher(Views.ADMIN_BOOKINGS)
                    .forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to load Admin bookings", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (!"checkIn".equals(request.getParameter("operation"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Integer bookingID = positiveID(request.getParameter("bookingId"));
        if (bookingID == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            AdminBookingService.ActionResult result = service.checkIn(bookingID);
            flash(request, !result.isSuccess(), result.getMessage());
            response.sendRedirect(request.getContextPath()
                    + "/MainController?action=adminBookings&id=" + bookingID);
        } catch (SQLException exception) {
            throw new ServletException("Unable to check in booking", exception);
        }
    }

    private void flash(HttpServletRequest request, boolean error, String message) {
        request.getSession().setAttribute(
                error ? "adminFlashError" : "adminFlash", message);
    }

    private void consumeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        Object success = session.getAttribute("adminFlash");
        Object error = session.getAttribute("adminFlashError");
        if (success != null) request.setAttribute("success", success);
        if (error != null) request.setAttribute("error", error);
        session.removeAttribute("adminFlash");
        session.removeAttribute("adminFlashError");
    }

    private Integer positiveID(String value) {
        try {
            int id = Integer.parseInt(value == null ? "" : value.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException exception) { return null; }
    }

    private boolean requireAdmin(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (!SessionUtil.isAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
