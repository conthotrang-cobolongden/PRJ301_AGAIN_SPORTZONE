package com.sportzone.controller;

import com.sportzone.model.Account;
import com.sportzone.service.ProfileService;
import com.sportzone.util.AppConstants;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * U6: đổi mật khẩu — CSRF bắt buộc, validate server-side (service),
 * thành công → PRG redirect /password?saved=1.
 */
public class PasswordServlet extends HttpServlet {

    private static final String MSG_SAVED = "Đổi mật khẩu thành công.";

    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("1".equals(request.getParameter("saved"))) {
            request.setAttribute(AppConstants.ATTR_SUCCESS, MSG_SAVED);
        }
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.PASSWORD).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ");
            return;
        }

        Account current = (Account) request.getSession().getAttribute(AppConstants.SESSION_ACCOUNT);
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        ProfileService.ProfileResult result;
        try {
            result = profileService.changePassword(current.getAccountId(),
                    currentPassword, newPassword, confirmPassword);
        } catch (SQLException ex) {
            throw new ServletException("Lỗi đổi mật khẩu: " + ex.getMessage(), ex);
        }

        if (!result.isSuccess()) {
            request.setAttribute(AppConstants.ATTR_FIELD_ERRORS, result.getFieldErrors());
            if (result.getGeneralError() != null) {
                request.setAttribute(AppConstants.ATTR_ERROR, result.getGeneralError());
            }
            CsrfUtil.ensureToken(request.getSession());
            request.getRequestDispatcher(Views.PASSWORD).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/password?saved=1");
    }
}
