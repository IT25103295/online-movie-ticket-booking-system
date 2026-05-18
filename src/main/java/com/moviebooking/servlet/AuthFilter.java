package com.moviebooking.servlet;

import com.moviebooking.service.AuthService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/showtimes",
        "/seats",
        "/checkout",
        "/payment",
        "/booking-result",
        "/my-bookings",
        "/ticket",
        
})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        AuthService authService = new AuthService(httpRequest.getServletContext());

        if (!authService.isLoggedIn(httpRequest.getSession(false))) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String path = httpRequest.getServletPath();
        if (path.startsWith("/admin") && !authService.isAdmin(httpRequest.getSession(false))) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to access this page.");
            return;
        }

        chain.doFilter(request, response);
    }
}
