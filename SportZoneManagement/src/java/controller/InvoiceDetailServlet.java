package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Account;
import service.InvoiceService;
import utils.AppConstants;
import utils.SessionUtil;
import utils.Views;

/** Shows an owned invoice only after it is Paid. */
public class InvoiceDetailServlet extends HttpServlet {

    private final InvoiceService invoiceService = new InvoiceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = SessionUtil.getLoggedInAccount(request.getSession(false));
        if (account == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!AppConstants.ROLE_USER.equals(account.getRoleName())
                && !AppConstants.ROLE_ADMIN.equals(account.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Integer bookingID = positiveID(request.getParameter("bookingId"));
        if (bookingID == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            InvoiceService.InvoiceDetail detail = invoiceService.getPaidDetail(
                    bookingID, account.getAccountID());
            if (detail == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("invoice", detail.getInvoice());
            request.setAttribute("serviceLines", detail.getServiceLines());
            request.getRequestDispatcher(Views.INVOICE_DETAIL)
                    .forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to load invoice detail", exception);
        }
    }

    private Integer positiveID(String raw) {
        try {
            int value = Integer.parseInt(raw == null ? "" : raw.trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
