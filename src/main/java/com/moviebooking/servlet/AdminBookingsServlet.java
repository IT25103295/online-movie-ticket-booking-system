package com.moviebooking.servlet;

import com.moviebooking.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/bookings")
public class AdminBookingsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService adminService = new AdminService(getServletContext());
        request.setAttribute("bookings", adminService.getRecentBookings());
        request.setAttribute("hasQueueData", adminService.hasQueueData());
        request.getRequestDispatcher("/WEB-INF/views/admin-bookings.jsp").forward(request, response);
    }
}
