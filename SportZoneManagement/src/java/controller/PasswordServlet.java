package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import service.ProfileService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

/** Shared Admin/User password change page. Tag: U6. */
public class PasswordServlet extends HttpServlet {

    private final ProfileService profileService;

    public PasswordServlet() {
        this(new ProfileService());
    }

    PasswordServlet(ProfileService profileService) {
        if (profileService == null) {
            throw new IllegalArgumentException("ProfileService must not be null");
        }
        this.profileService = profileService;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (currentAccount(request) == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        CsrfUtil.ensureToken(request.getSession());
        if ("1".equals(request.getParameter("changed"))) {
            request.setAttribute("success", "Đổi mật khẩu thành công.");
        }
        request.getRequestDispatcher(Views.PASSWORD).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Account currentAccount = currentAccount(request);
        if (currentAccount == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        ProfileService.ChangePasswordResult result;
        try {
            result = profileService.changePassword(
                    currentAccount.getAccountID(), currentPassword,
                    newPassword, confirmPassword);
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }

        if (!result.isSuccess()) {
            request.setAttribute(AppConstants.ATTRIBUTE_FIELD_ERRORS,
                    result.getFieldErrors());
            CsrfUtil.ensureToken(request.getSession());
            request.getRequestDispatcher(Views.PASSWORD)
                    .forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/password?changed=1");
    }

    private Account currentAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return SessionUtil.getLoggedInAccount(session);
    }
}
