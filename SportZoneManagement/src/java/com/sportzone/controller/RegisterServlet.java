package com.sportzone.controller;

import com.sportzone.service.AuthService;
import com.sportzone.service.AuthService.RegisterResult;
import com.sportzone.util.AppConstants;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.SessionUtil;
import com.sportzone.util.Validation;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request.getSession(false))) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.REGISTER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ");
            return;
        }

        String fullName = Validation.trimToNull(request.getParameter("fullName"));
        String email = Validation.trimToNull(request.getParameter("email"));
        String phone = Validation.trimToNull(request.getParameter("phone"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        RegisterResult result;
        try {
            result = authService.register(fullName, email, phone, password, confirmPassword);
        } catch (SQLException ex) {
            // Pre-check existsByEmail/existsByPhone là check-then-act: hai request song song
            // có thể cùng vượt qua pre-check. Unique index của DB là chốt chặn cuối —
            // bắt SQLState 23000 (integrity constraint violation) và báo lỗi thân thiện.
            if (isUniqueViolation(ex)) {
                Map<String, String> errors = new LinkedHashMap<>();
                errors.put("email", "Email hoặc số điện thoại đã được sử dụng");
                renderFormWithErrors(request, response, fullName, email, phone, errors);
                return;
            }
            throw new ServletException("Lỗi đăng ký: " + ex.getMessage(), ex);
        }

        if (!result.isSuccess()) {
            renderFormWithErrors(request, response, fullName, email, phone, result.getFieldErrors());
            return;
        }

        // Đăng ký thành công: tự đăng nhập luôn rồi về /home
        SessionUtil.storeLoggedInAccount(request.getSession(), result.getAccount());
        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void renderFormWithErrors(HttpServletRequest request, HttpServletResponse response,
                                      String fullName, String email, String phone,
                                      Map<String, String> fieldErrors)
            throws ServletException, IOException {
        // Chỉ giữ lại fullName/email/phone — không echo password lên form
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute(AppConstants.ATTR_FIELD_ERRORS, fieldErrors);
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.REGISTER).forward(request, response);
    }

    private boolean isUniqueViolation(SQLException ex) {
        return "23000".equals(ex.getSQLState());
    }
}
