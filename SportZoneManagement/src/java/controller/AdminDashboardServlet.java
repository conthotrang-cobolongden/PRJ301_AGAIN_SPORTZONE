package controller;

import dao.AdminDashboardDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.AdminBookingService;
import utils.SessionUtil;
import utils.Views;

public class AdminDashboardServlet extends HttpServlet {

    private final AdminDashboardDAO dashboardDAO = new AdminDashboardDAO();
    private final AdminBookingService bookingService = new AdminBookingService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;
        try {
            bookingService.reconcile();
            request.setAttribute("dashboard", dashboardDAO.load());
            request.getRequestDispatcher(Views.ADMIN_DASHBOARD)
                    .forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to load Admin dashboard", exception);
        }
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
