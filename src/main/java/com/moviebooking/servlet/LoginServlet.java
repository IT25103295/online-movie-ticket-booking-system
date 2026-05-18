package com.moviebooking.servlet;

import com.moviebooking.model.User;
import com.moviebooking.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");

        AuthService authService = new AuthService(getServletContext());
        User user = authService.validateCredentials(usernameOrEmail, password);
        if (user == null) {
            request.setAttribute("error", "Invalid username/email or password.");
            request.setAttribute("usernameOrEmail", usernameOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        request.getSession(true).setAttribute(AuthService.CURRENT_USER_SESSION_KEY, user);
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            response.sendRedirect(request.getContextPath() + "/movies");
        }
    }
}
