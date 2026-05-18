package com.moviebooking.servlet;

import com.moviebooking.model.Booking;
import com.moviebooking.service.BookingHistoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ticket")
public class TicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bookingId = request.getParameter("bookingId");
        Booking booking = new BookingHistoryService(getServletContext()).getBookingById(bookingId);

        if (booking == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found.");
            return;
        }

        request.setAttribute("booking", booking);
        request.getRequestDispatcher("/WEB-INF/views/ticket.jsp").forward(request, response);
    }
}
