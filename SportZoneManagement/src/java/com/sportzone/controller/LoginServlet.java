package com.sportzone.controller;

import com.sportzone.model.Account;
import com.sportzone.service.AuthService;
import com.sportzone.util.AppConstants;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.SessionUtil;
import com.sportzone.util.Validation;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    private static final String MSG_INVALID_CREDENTIALS = "Email hoặc mật khẩu không đúng.";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request.getSession(false))) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ");
            return;
        }

        String email = Validation.trimToNull(request.getParameter("email"));
        String password = request.getParameter("password");

        Account account;
        try {
            account = authService.login(email, password);
        } catch (SQLException ex) {
            throw new ServletException("Lỗi đăng nhập: " + ex.getMessage(), ex);
        }

        if (account == null) {
            request.setAttribute(AppConstants.ATTR_ERROR, MSG_INVALID_CREDENTIALS);
            request.setAttribute("email", email);
            CsrfUtil.ensureToken(request.getSession());
            request.getRequestDispatcher(Views.LOGIN).forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        SessionUtil.storeLoggedInAccount(session, account);
        response.sendRedirect(request.getContextPath() + resolveTarget(session));
    }

    /**
     * Ưu tiên trả user về đúng trang họ định vào trước khi bị AuthFilter chặn
     * (key redirectAfterLogin, xóa ngay sau khi dùng). Nếu không có thì về theo role:
     * hiện tại cả Admin lẫn User đều về /home vì /admin chưa tồn tại đến P8 —
     * khi làm P8 chỉ cần đổi nhánh Admin ở đây thành "/admin".
     */
    private String resolveTarget(HttpSession session) {
        String savedTarget = (String) session.getAttribute(AppConstants.SESSION_REDIRECT_AFTER_LOGIN);
        if (savedTarget != null && !savedTarget.isEmpty()) {
            session.removeAttribute(AppConstants.SESSION_REDIRECT_AFTER_LOGIN);
            return savedTarget;
        }
        return "/home";
    }
}
