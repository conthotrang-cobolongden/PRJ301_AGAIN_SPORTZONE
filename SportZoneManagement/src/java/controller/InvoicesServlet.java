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

/** Lists only Paid invoices owned by the logged-in User. */
public class InvoicesServlet extends HttpServlet {

    private final InvoiceService invoiceService = new InvoiceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = SessionUtil.getLoggedInAccount(request.getSession(false));
        if (account == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!AppConstants.ROLE_USER.equals(account.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            request.setAttribute("invoiceList",
                    invoiceService.listPaid(account.getAccountID()));
            request.getRequestDispatcher(Views.INVOICE_LIST)
                    .forward(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to list invoices", exception);
        }
    }
}
