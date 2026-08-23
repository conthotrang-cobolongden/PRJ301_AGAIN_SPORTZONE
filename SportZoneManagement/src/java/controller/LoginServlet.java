package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import service.AuthService;
import utils.AppConstants;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

/**
 * Handles login form display and credential submission.
 */
public class LoginServlet extends HttpServlet {

    private final AuthService authService;

    public LoginServlet() {
        this(new AuthService());
    }

    LoginServlet(AuthService authService) {
        if (authService == null) {
            throw new IllegalArgumentException("AuthService must not be null");
        }
        this.authService = authService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession existingSession
                = request.getSession(false);

        if (SessionUtil.isLoggedIn(existingSession)) {
            String target = SessionUtil.isFrozen(existingSession)
                    ? "/MainController?action=accountLocked"
                    : "/MainController?action=home";

            response.sendRedirect(
                    request.getContextPath() + target
            );
            return;
        }

        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String submittedEmail = request.getParameter("email");
        String email = submittedEmail == null ? null : submittedEmail.trim();
        String password = request.getParameter("password");
        Account account;
        try {
            account = authService.login(email, password);
        } catch (SQLException exception) {
            throw new ServletException(exception);
        }

        if (account == null) {
            request.setAttribute(AppConstants.ATTRIBUTE_ERROR_MESSAGE,
                    "Email hoặc mật khẩu không đúng.");
            request.setAttribute("email", email);
            CsrfUtil.ensureToken(request.getSession());
            request.getRequestDispatcher(Views.LOGIN).forward(request, response);
            return;
        }

        HttpSession session = request.getSession();

        SessionUtil.storeLoggedInAccount(
                session,
                account
        );

        if (SessionUtil.isFrozen(session)) {
            session.removeAttribute(
                    AppConstants.SESSION_REDIRECT_AFTER_LOGIN
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/MainController?action=accountLocked"
            );
            return;
        }

        consumeTarget(
                request,
                response,
                session
        );
    }

    private void consumeTarget(HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws IOException {
        Object savedTarget = session.getAttribute(
                AppConstants.SESSION_REDIRECT_AFTER_LOGIN);
        session.removeAttribute(AppConstants.SESSION_REDIRECT_AFTER_LOGIN);
        String target = savedTarget instanceof String
                && ((String) savedTarget).startsWith("/")
                ? (String) savedTarget
                : "/MainController?action=home";
        response.sendRedirect(request.getContextPath() + target);
    }
}
