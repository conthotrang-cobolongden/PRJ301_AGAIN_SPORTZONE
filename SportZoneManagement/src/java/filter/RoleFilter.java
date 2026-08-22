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
import utils.SessionUtil;

/** Chi cho phep tai khoan Admin truy cap tai nguyen duoc bao ve. */
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (SessionUtil.isAdmin(httpRequest.getSession(false))) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public void destroy() { }
}
