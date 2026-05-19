package com.moviebooking.servlet;

import com.moviebooking.model.User;
import com.moviebooking.service.AuthService;
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in and is admin
        boolean isAdmin = false;

        if (session != null) {
            // First try to get from CURRENT_USER_SESSION_KEY
            User currentUser = (User) session.getAttribute(AuthService.CURRENT_USER_SESSION_KEY);
            if (currentUser != null && currentUser.isAdmin()) {
                isAdmin = true;
            }

            // Fallback to old method for compatibility
            if (!isAdmin) {
                Object role = session.getAttribute("role");
                Object isAdminAttr = session.getAttribute("isAdmin");
                isAdmin = "ADMIN".equals(String.valueOf(role)) || Boolean.TRUE.equals(isAdminAttr);
            }
        }

        // DEMO MODE: Allow ?admin=true parameter for testing
        if (!isAdmin && "true".equalsIgnoreCase(httpRequest.getParameter("admin"))) {
            isAdmin = true;
            if (session != null) {
                session.setAttribute("role", "ADMIN");
                session.setAttribute("isAdmin", Boolean.TRUE);
            }
        }

        if (!isAdmin) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required. Please login with admin credentials.");
            return;
        }

        chain.doFilter(request, response);
    }
}