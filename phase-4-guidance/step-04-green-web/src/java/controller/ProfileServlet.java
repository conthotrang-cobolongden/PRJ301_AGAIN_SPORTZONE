package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
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

/** Shared Admin/User profile page. Tags: U4, U5. */
public class ProfileServlet extends HttpServlet {

    private static final DateTimeFormatter MEMBER_SINCE_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ProfileService profileService;

    public ProfileServlet() {
        this(new ProfileService());
    }

    ProfileServlet(ProfileService profileService) {
        if (profileService == null) {
            throw new IllegalArgumentException("ProfileService must not be null");
        }
        this.profileService = profileService;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Account currentAccount = currentAccount(request);
        if (currentAccount == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        CsrfUtil.ensureToken(request.getSession());
        request.setAttribute("account", currentAccount);
        bindMemberSince(request, currentAccount);
        if ("1".equals(request.getParameter("updated"))) {
            request.setAttribute("success", "Cập nhật hồ sơ thành công.");
        }
        request.getRequestDispatcher(Views.PROFILE).forward(request, response);
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

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        ProfileService.UpdateProfileResult result;
        try {
            result = profileService.updateProfile(currentAccount.getAccountID(),
                    fullName, email, phone);
        } catch (SQLException exception) {
            if ("23000".equals(exception.getSQLState())) {
                LinkedHashMap<String, String> errors = new LinkedHashMap<>();
                errors.put("email", "Email hoặc số điện thoại đã được sử dụng");
                renderErrors(request, response, currentAccount,
                        fullName, email, phone, errors);
                return;
            }
            throw new ServletException(exception);
        }

        if (!result.isSuccess()) {
            renderErrors(request, response, currentAccount,
                    fullName, email, phone, result.getFieldErrors());
            return;
        }

        SessionUtil.storeLoggedInAccount(
                request.getSession(), result.getAccount());
        response.sendRedirect(request.getContextPath() + "/profile?updated=1");
    }

    private Account currentAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return SessionUtil.getLoggedInAccount(session);
    }

    private void renderErrors(HttpServletRequest request,
            HttpServletResponse response, Account currentAccount,
            String fullName, String email, String phone,
            Map<String, String> errors) throws ServletException, IOException {
        Account formAccount = copyForForm(currentAccount);
        formAccount.setFullName(fullName);
        formAccount.setEmail(email);
        formAccount.setPhone(phone);

        request.setAttribute("account", formAccount);
        request.setAttribute(AppConstants.ATTRIBUTE_FIELD_ERRORS, errors);
        bindMemberSince(request, currentAccount);
        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.PROFILE).forward(request, response);
    }

    private Account copyForForm(Account source) {
        Account copy = new Account();
        copy.setAccountID(source.getAccountID());
        copy.setRoleID(source.getRoleID());
        copy.setRoleName(source.getRoleName());
        copy.setStatus(source.getStatus());
        copy.setLastLoginAt(source.getLastLoginAt());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setFullName(source.getFullName());
        copy.setEmail(source.getEmail());
        copy.setPhone(source.getPhone());
        return copy;
    }

    private void bindMemberSince(HttpServletRequest request, Account account) {
        if (account.getCreatedAt() != null) {
            request.setAttribute("memberSince",
                    account.getCreatedAt().format(MEMBER_SINCE_FORMAT));
        }
    }
}
