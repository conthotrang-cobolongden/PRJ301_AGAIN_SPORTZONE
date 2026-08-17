package com.sportzone.filter;

import com.sportzone.util.AppConstants;
import com.sportzone.util.SessionUtil;
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

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (SessionUtil.isLoggedIn(httpRequest.getSession(false))) {
            chain.doFilter(request, response);
            return;
        }

        // Chỉ lưu trang định vào với GET: POST không replay lại được form sau login,
        // và target được lấy từ chính request nên luôn nằm trong app.
        if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            HttpSession session = httpRequest.getSession();
            session.setAttribute(AppConstants.SESSION_REDIRECT_AFTER_LOGIN, buildTarget(httpRequest));
        }
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    private String buildTarget(HttpServletRequest request) {
        String target = request.getServletPath();
        String query = request.getQueryString();
        return query == null ? target : target + "?" + query;
    }

    @Override
    public void destroy() {
    }
}
