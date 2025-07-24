package vd.vendordebtors.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login.jsp", "/register.jsp", "/login", "/register",
            "/css/", "/js/", "/images/", "/error/", "/400.jsp", "/404.jsp", "/500.jsp"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String requestPath = req.getRequestURI().substring(req.getContextPath().length());

        if ("/home.jsp".equals(requestPath)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        HttpSession session = req.getSession(false);
        boolean isAuthenticated = session != null && session.getAttribute("vendor") != null;
        if (isAuthenticated && (requestPath.equals("/login.jsp") || requestPath.equals("/register.jsp") ||
                requestPath.equals("/login") || requestPath.equals("/register"))) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        if (isPublicPath(requestPath)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (!isAuthenticated) {
            if (isAjaxRequest(req)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Session expired\"}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(publicPath ->
                path.equals(publicPath) || path.startsWith(publicPath)
        );
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}
