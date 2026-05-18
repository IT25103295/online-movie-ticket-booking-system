package com.moviebooking.servlet;

import com.moviebooking.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        request.setAttribute("name", name);
        request.setAttribute("username", username);
        request.setAttribute("email", email);

        String error = validateForm(name, username, email, password, confirmPassword);
        AuthService authService = new AuthService(getServletContext());

        if (error == null && authService.findByUsername(username) != null) {
            error = "Username is already taken.";
        }

        if (error == null && authService.findByEmail(email) != null) {
            error = "Email is already registered.";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
            return;
        }

        authService.registerUser(name, username, email, password);
        response.sendRedirect(request.getContextPath() + "/login?registered=1");
    }

    private String validateForm(String name, String username, String email, String password, String confirmPassword) {
        if (isBlank(name) || isBlank(username) || isBlank(email) || isBlank(password) || isBlank(confirmPassword)) {
            return "Please fill in all required fields.";
        }

        if (!password.equals(confirmPassword)) {
            return "Password and confirmation password do not match.";
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
