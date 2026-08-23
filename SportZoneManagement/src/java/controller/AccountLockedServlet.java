package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import utils.AppConstants;
import utils.SessionUtil;
import utils.Views;

/**
 * Trang thong bao tai khoan bi khoa.
 */
public class AccountLockedServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session
                = request.getSession(false);

        Account account
                = SessionUtil.getLoggedInAccount(session);

        if (account == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        boolean frozen
                = SessionUtil.isFrozen(session)
                || AppConstants.ACCOUNT_FROZEN.equals(
                        account.getStatus()
                );

        if (!frozen) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/MainController?action=home"
            );
            return;
        }

        request.setAttribute("account", account);

        request.getRequestDispatcher(
                Views.ACCOUNT_LOCKED
        ).forward(request, response);
    }
}
