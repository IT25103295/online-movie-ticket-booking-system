package com.moviebooking.servlet;

import com.moviebooking.service.BookingHistoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/my-bookings")
public class MyBookingsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customerEmail = getCustomerEmail(request.getSession(false));
        BookingHistoryService bookingHistoryService = new BookingHistoryService(getServletContext());

        request.setAttribute("customerEmail", customerEmail);
        request.setAttribute("bookings", bookingHistoryService.getBookingsForUser(customerEmail));
        request.getRequestDispatcher("/WEB-INF/views/my-bookings.jsp").forward(request, response);
    }

    private String getCustomerEmail(HttpSession session) {
        if (session == null) {
            return BookingHistoryService.DEMO_CUSTOMER_EMAIL;
        }

        Object customerEmail = session.getAttribute("customerEmail");
        if (customerEmail == null || customerEmail.toString().trim().isEmpty()) {
            return BookingHistoryService.DEMO_CUSTOMER_EMAIL;
        }
        return customerEmail.toString();
    }
}
