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
import utils.AppConstants;
import utils.SessionUtil;

/** Yeu cau dang nhap truoc khi truy cap tai nguyen duoc bao ve. */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isPublicMainControllerAction(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        if (SessionUtil.isLoggedIn(httpRequest.getSession(false))) {
            chain.doFilter(request, response);
            return;
        }

        if ("GET".equals(httpRequest.getMethod())) {
            String target = httpRequest.getServletPath();
            String query = httpRequest.getQueryString();
            if (query != null) {
                target += "?" + query;
            }
            HttpSession session = httpRequest.getSession();
            // Chi luu GET vi co the phat lai an toan; servlet path giu dich trong noi bo ung dung.
            session.setAttribute(AppConstants.SESSION_REDIRECT_AFTER_LOGIN, target);
        }

        httpResponse.sendRedirect(
                httpRequest.getContextPath()
                + "/MainController?action=login"
        );
    }

    private boolean isPublicMainControllerAction(
            HttpServletRequest request
    ) {
        if (!"/MainController".equals(
                request.getServletPath()
        )) {
            return false;
        }

        String action = request.getParameter("action");
        return !"profile".equals(action)
                && !"password".equals(action)
                && !"bookingCreate".equals(action)
                && !"bookings".equals(action)
                && !"bookingDetail".equals(action)
                && !"accountLocked".equals(action)
                && !"invoicePayment".equals(action)
                && !"invoices".equals(action)
                && !"invoiceDetail".equals(action)
                && !"adminDashboard".equals(action)
                && !"adminCourts".equals(action)
                && !"adminBookings".equals(action);
    }

    @Override
    public void destroy() { }
}
