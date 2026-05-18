package com.moviebooking.servlet;

import com.moviebooking.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/queue")
public class AdminQueueServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BookingService bookingService = new BookingService(getServletContext());
        request.setAttribute("queueSize", bookingService.getQueueSize());
        request.setAttribute("pendingRequests", bookingService.getPendingRequests());
        request.setAttribute("processedRequests", bookingService.getProcessedRequests());
        request.setAttribute("lastProcessed", bookingService.getLastProcessed());
        request.getRequestDispatcher("/WEB-INF/views/admin-queue.jsp").forward(request, response);
    }
}
