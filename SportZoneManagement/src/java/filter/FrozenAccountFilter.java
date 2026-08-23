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
 * - Xem /account-locked
 * - Logout
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

        String servletPath
                = httpRequest.getServletPath();

        if (isAllowedPath(servletPath)) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(
                httpRequest.getContextPath()
                + "/account-locked"
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

    private boolean isAllowedPath(String servletPath) {
        if (servletPath == null) {
            return false;
        }

        return "/account-locked".equals(servletPath)
                || "/logout".equals(servletPath)
                || servletPath.startsWith("/assets/");
    }

    @Override
    public void destroy() {
    }
}