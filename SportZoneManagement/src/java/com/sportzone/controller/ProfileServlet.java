package com.sportzone.controller;

import com.sportzone.model.Account;
import com.sportzone.service.ProfileService;
import com.sportzone.util.AppConstants;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.SessionUtil;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * U4: xem hồ sơ + U5: cập nhật họ tên/email/SĐT (Admin dùng chung — tự sửa mình).
 * POST thành công → PRG redirect /profile?saved=1 để tránh resubmit.
 */
public class ProfileServlet extends HttpServlet {

    private static final String MSG_SAVED = "Đã lưu thay đổi hồ sơ.";

    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("1".equals(request.getParameter("saved"))) {
            request.setAttribute(AppConstants.ATTR_SUCCESS, MSG_SAVED);
        }
        // Form lấy giá trị hiện tại từ account trong session
        request.setAttribute("account", request.getSession().getAttribute(AppConstants.SESSION_ACCOUNT));
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.PROFILE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ");
            return;
        }

        HttpSession session = request.getSession();
        Account current = (Account) session.getAttribute(AppConstants.SESSION_ACCOUNT);

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        ProfileService.ProfileResult result;
        try {
            result = profileService.updateProfile(current.getAccountId(), fullName, email, phone);
        } catch (SQLException ex) {
            // Race condition trùng email/phone (UNIQUE) — pre-check đã có, đây là chốt cuối
            if ("23000".equals(ex.getSQLState())) {
                request.setAttribute(AppConstants.ATTR_ERROR, "Email hoặc số điện thoại đã được sử dụng.");
                renderForm(request, response, fullName, email, phone);
                return;
            }
            throw new ServletException("Lỗi cập nhật hồ sơ: " + ex.getMessage(), ex);
        }

        if (!result.isSuccess()) {
            request.setAttribute(AppConstants.ATTR_FIELD_ERRORS, result.getFieldErrors());
            if (result.getGeneralError() != null) {
                request.setAttribute(AppConstants.ATTR_ERROR, result.getGeneralError());
            }
            renderForm(request, response, fullName, email, phone);
            return;
        }

        // Cập nhật lại account trong session để header/profile hiển thị tên mới ngay
        SessionUtil.storeLoggedInAccount(session, result.getAccount());
        response.sendRedirect(request.getContextPath() + "/profile?saved=1");
    }

    /** Render lại form với giá trị user vừa nhập (không bao giờ để trống sau lỗi). */
    private void renderForm(HttpServletRequest request, HttpServletResponse response,
                           String fullName, String email, String phone)
            throws ServletException, IOException {
        Account typed = new Account();
        typed.setFullName(fullName);
        typed.setEmail(email);
        typed.setPhone(phone);
        request.setAttribute("account", typed);
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.PROFILE).forward(request, response);
    }
}
