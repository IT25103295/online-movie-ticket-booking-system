package com.moviebooking.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AdminAccessFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        if ("true".equalsIgnoreCase(httpRequest.getParameter("admin"))) {
            session.setAttribute("role", "ADMIN");
            session.setAttribute("isAdmin", Boolean.TRUE);
        }

        Object role = session.getAttribute("role");
        Object isAdmin = session.getAttribute("isAdmin");
        boolean allowed = "ADMIN".equals(String.valueOf(role)) || Boolean.TRUE.equals(isAdmin);

        if (!allowed) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required. Use ?admin=true for demo access when AuthService is unavailable.");
            return;
        }

        chain.doFilter(request, response);
    }
}
