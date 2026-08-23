package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.AuthService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

/** Handles registration form display and account creation. */
public class RegisterServlet extends HttpServlet {

    private final AuthService authService;

    public RegisterServlet() {
        this(new AuthService());
    }

    RegisterServlet(AuthService authService) {
        if (authService == null) {
            throw new IllegalArgumentException("AuthService must not be null");
        }
        this.authService = authService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request.getSession(false))) {
            response.sendRedirect(request.getContextPath()
                    + "/MainController?action=home");
            return;
        }

        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.REGISTER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        AuthService.RegisterResult result;
        try {
            result = authService.register(
                    fullName, email, phone, password, confirmPassword);
        } catch (SQLException exception) {
            if ("23000".equals(exception.getSQLState())) {
                // Kiểm tra trước không thể ngăn hai yêu cầu đồng thời cùng chèn dữ liệu trùng.
                LinkedHashMap<String, String> errors = new LinkedHashMap<>();
                errors.put("email", "Email hoặc số điện thoại đã được sử dụng");
                renderErrors(request, response, fullName, email, phone, errors);
                return;
            }
            throw new ServletException(exception);
        }

        if (!result.isSuccess()) {
            renderErrors(request, response, fullName, email, phone,
                    result.getFieldErrors());
            return;
        }

        SessionUtil.storeLoggedInAccount(request.getSession(), result.getAccount());
        response.sendRedirect(request.getContextPath()
                + "/MainController?action=home");
    }

    private void renderErrors(HttpServletRequest request,
            HttpServletResponse response, String fullName, String email,
            String phone, Map<String, String> errors)
            throws ServletException, IOException {
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute(AppConstants.ATTRIBUTE_FIELD_ERRORS, errors);
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.REGISTER).forward(request, response);
    }
}
