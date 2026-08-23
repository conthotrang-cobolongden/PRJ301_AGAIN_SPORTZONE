package controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Front Controller chi lam nhiem vu dieu huong request.
 * Tag: P1
 */
public class MainController extends HttpServlet {

    private String resolveServletName(String action) {
        if (action == null) {
            return "HomeServlet";
        }

        switch (action) {
            case "home":
                return "HomeServlet";
            case "health":
                return "HealthServlet";
            case "login":
                return "LoginServlet";
            case "register":
                return "RegisterServlet";
            case "logout":
                return "LogoutServlet";
            case "courts":
                return "CourtsServlet";
            case "court":
                return "CourtDetailServlet";
            case "profile":
                return "ProfileServlet";
            case "password":
                return "PasswordServlet";
            case "bookingCreate":
                return "BookingCreateServlet";
            case "bookings":
                return "BookingsServlet";
            case "bookingDetail":
                return "BookingDetailServlet";
            case "accountLocked":
                return "AccountLockedServlet";
            case "invoicePayment":
                return "InvoicePaymentServlet";
            case "invoices":
                return "InvoicesServlet";
            case "invoiceDetail":
                return "InvoiceDetailServlet";
            case "adminDashboard":
                return "AdminDashboardServlet";
            case "adminCourts":
                return "AdminCourtsServlet";
            case "adminBookings":
                return "AdminBookingsServlet";
            default:
                return null;
        }
    }

    protected void processRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String servletName = resolveServletName(
                request.getParameter("action")
        );

        if (servletName == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        RequestDispatcher dispatcher
                = getServletContext().getNamedDispatcher(servletName);

        if (dispatcher == null) {
            throw new ServletException(
                    "No servlet is declared with name: " + servletName
            );
        }

        dispatcher.forward(request, response);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        processRequest(request, response);
    }
}
