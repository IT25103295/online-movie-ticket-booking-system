package com.moviebooking.servlet;

import com.moviebooking.model.BookingRequest;
import com.moviebooking.model.BookingStatus;
import com.moviebooking.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final double SEAT_PRICE = 1500.00;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        prepareCheckoutSummary(request);
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String showtimeId = session == null ? null : (String) session.getAttribute("selectedShowtimeId");
        ArrayList<String> selectedSeats = readSelectedSeats(session);

        if (showtimeId == null || selectedSeats.isEmpty()) {
            request.setAttribute("error", "Please select seats before confirming a booking.");
            prepareCheckoutSummary(request);
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        String customerName = request.getParameter("customerName");
        String customerEmail = request.getParameter("customerEmail");
        if (isBlank(customerName) || isBlank(customerEmail)) {
            request.setAttribute("error", "Please enter your name and email address.");
            prepareCheckoutSummary(request);
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        String movieId = session == null ? null : (String) session.getAttribute("selectedMovieId");
        BookingRequest bookingRequest = new BookingRequest("", customerName, customerEmail, defaultText(movieId, "Movie N/A"),
                showtimeId, selectedSeats, selectedSeats.size() * SEAT_PRICE, BookingStatus.PENDING,
                LocalDateTime.now().toString(), "", "-");

        BookingService bookingService = new BookingService(getServletContext());
        BookingRequest queuedRequest = bookingService.enqueueBooking(bookingRequest);
        BookingRequest processedRequest = bookingService.processNext();
        String resultRequestId = processedRequest == null ? queuedRequest.getRequestId() : processedRequest.getRequestId();

        session.removeAttribute("selectedShowtimeId");
        session.removeAttribute("selectedSeats");
        response.sendRedirect(request.getContextPath() + "/booking-result?requestId=" + resultRequestId);
    }

    private void prepareCheckoutSummary(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String showtimeId = session == null ? null : (String) session.getAttribute("selectedShowtimeId");
        ArrayList<String> selectedSeats = readSelectedSeats(session);
        String movieId = session == null ? null : (String) session.getAttribute("selectedMovieId");

        request.setAttribute("movieLabel", defaultText(movieId, "Selected Movie"));
        request.setAttribute("showtimeId", showtimeId);
        request.setAttribute("selectedSeats", selectedSeats);
        request.setAttribute("seatPrice", SEAT_PRICE);
        request.setAttribute("totalPrice", selectedSeats.size() * SEAT_PRICE);
        request.setAttribute("hasSelection", showtimeId != null && !selectedSeats.isEmpty());
    }

    private ArrayList<String> readSelectedSeats(HttpSession session) {
        ArrayList<String> selectedSeats = new ArrayList<>();
        if (session == null) {
            return selectedSeats;
        }

        Object sessionSeats = session.getAttribute("selectedSeats");
        if (sessionSeats instanceof List<?>) {
            for (Object seat : (List<?>) sessionSeats) {
                if (seat != null && !seat.toString().trim().isEmpty()) {
                    selectedSeats.add(seat.toString());
                }
            }
        } else if (sessionSeats instanceof String[]) {
            selectedSeats.addAll(Arrays.asList((String[]) sessionSeats));
        } else if (sessionSeats instanceof String) {
            String[] parts = ((String) sessionSeats).split(",");
            selectedSeats.addAll(Arrays.asList(parts));
        }
        return selectedSeats;
    }

    private String defaultText(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
