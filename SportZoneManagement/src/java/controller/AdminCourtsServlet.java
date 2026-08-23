package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import service.AdminCourtService;
import service.WeeklySlotService;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

public class AdminCourtsServlet extends HttpServlet {

    private final AdminCourtService courtService = new AdminCourtService();
    private final WeeklySlotService slotService = new WeeklySlotService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;
        CsrfUtil.ensureToken(request.getSession());
        consumeFlash(request);
        request.setAttribute("defaultWeek", LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY)).toString());
        try {
            request.setAttribute("courtList", courtService.list());
            request.getRequestDispatcher(Views.ADMIN_COURTS)
                    .forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to load Admin courts", exception);
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
        String operation = request.getParameter("operation");
        try {
            if ("updateCourt".equals(operation)) {
                Integer courtID = positiveID(request.getParameter("courtId"));
                if (courtID == null) {
                    flash(request, true, "Sân không hợp lệ.");
                } else {
                    AdminCourtService.UpdateResult result = courtService.update(
                            courtID, request.getParameter("price"),
                            request.getParameter("status"), LocalDateTime.now());
                    flash(request, !result.isSuccess(), result.getMessage());
                }
            } else if ("generateWeek".equals(operation)) {
                LocalDate monday = parseDate(request.getParameter("weekStart"));
                WeeklySlotService.GenerateResult result
                        = slotService.generate(monday);
                flash(request, !result.isSuccess(), result.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            response.sendRedirect(request.getContextPath()
                    + "/MainController?action=adminCourts");
        } catch (SQLException exception) {
            throw new ServletException("Unable to update Admin courts", exception);
        }
    }

    private void flash(HttpServletRequest request, boolean error, String message) {
        HttpSession session = request.getSession();
        session.setAttribute(error ? "adminFlashError" : "adminFlash", message);
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

    private LocalDate parseDate(String value) {
        try { return LocalDate.parse(value == null ? "" : value.trim()); }
        catch (RuntimeException exception) { return null; }
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
