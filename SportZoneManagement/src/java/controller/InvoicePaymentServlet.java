package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Account;
import model.Invoice;
import service.InvoiceService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

/** GET/POST simulated bank-transfer payment for an owned booking. */
public class InvoicePaymentServlet extends HttpServlet {

    private final InvoiceService invoiceService;

    public InvoicePaymentServlet() {
        this(new InvoiceService());
    }

    InvoicePaymentServlet(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = requireUser(request, response);
        if (account == null) return;

        Integer bookingID = positiveID(request.getParameter("bookingId"));
        if (bookingID == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Invoice invoice = invoiceService.startPayment(
                    bookingID, account.getAccountID());
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if ("Paid".equals(invoice.getPaymentStatus())) {
                redirectDetail(request, response, bookingID);
                return;
            }
            CsrfUtil.ensureToken(request.getSession());
            request.setAttribute("invoice", invoice);
            request.getRequestDispatcher(Views.INVOICE_PAYMENT)
                    .forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to load payment", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Account account = requireUser(request, response);
        if (account == null) return;
        Integer bookingID = positiveID(request.getParameter("bookingId"));
        if (bookingID == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String operation = request.getParameter("operation");
        try {
            Invoice invoice;
            if ("pay".equals(operation)) {
                invoice = invoiceService.pay(bookingID, account.getAccountID());
            } else if ("cancel".equals(operation)) {
                invoice = invoiceService.cancel(bookingID, account.getAccountID());
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if ("Paid".equals(invoice.getPaymentStatus())) {
                redirectDetail(request, response, bookingID);
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/MainController?action=invoicePayment&bookingId="
                        + bookingID);
            }
        } catch (SQLException exception) {
            throw new ServletException("Unable to update payment", exception);
        }
    }

    private void redirectDetail(HttpServletRequest request,
            HttpServletResponse response, int bookingID) throws IOException {
        response.sendRedirect(request.getContextPath()
                + "/MainController?action=invoiceDetail&bookingId=" + bookingID);
    }

    private Account requireUser(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Account account = SessionUtil.getLoggedInAccount(request.getSession(false));
        if (account == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        if (!AppConstants.ROLE_USER.equals(account.getRoleName())
                && !AppConstants.ROLE_ADMIN.equals(account.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return account;
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
