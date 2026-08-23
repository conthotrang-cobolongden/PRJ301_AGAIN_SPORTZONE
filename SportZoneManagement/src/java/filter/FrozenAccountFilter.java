package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import utils.AppConstants;
import utils.SessionUtil;

/**
 * Cach ly tai khoan Frozen khoi toan bo he thong.
 *
 * Frozen chi duoc:
 * - Xem accountLocked qua MainController
 * - Logout qua MainController
 * - Tai static assets cua trang khoa
 */
public class FrozenAccountFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest
                = (HttpServletRequest) request;

        HttpServletResponse httpResponse
                = (HttpServletResponse) response;

        HttpSession session
                = httpRequest.getSession(false);

        Account account
                = SessionUtil.getLoggedInAccount(session);

        /*
         * Guest va account Active tiep tuc binh thuong.
         */
        if (account == null || !isFrozen(session, account)) {
            chain.doFilter(request, response);
            return;
        }

        if (isAllowedRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(
                httpRequest.getContextPath()
                + "/MainController?action=accountLocked"
        );
    }

    private boolean isFrozen(
            HttpSession session,
            Account account
    ) {
        return SessionUtil.isFrozen(session)
                || AppConstants.ACCOUNT_FROZEN.equals(
                        account.getStatus()
                );
    }

    private boolean isAllowedRequest(
            HttpServletRequest request
    ) {
        String servletPath = request.getServletPath();

        if (servletPath == null) {
            return false;
        }

        if ("/MainController".equals(servletPath)) {
            String action = request.getParameter("action");
            return "accountLocked".equals(action)
                    || "logout".equals(action);
        }

        return servletPath.startsWith("/assets/");
    }

    @Override
    public void destroy() {
    }
}
