package com.moviebooking.servlet;

import com.moviebooking.model.BookingRequest;
import com.moviebooking.model.BookingStatus;
import com.moviebooking.model.Payment;
import com.moviebooking.model.PaymentMethod;
import com.moviebooking.model.PaymentStatus;
import com.moviebooking.service.BookingService;
import com.moviebooking.service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!preparePaymentPage(request)) {
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !hasCheckoutData(session)) {
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        String requestId = getOrCreateBookingRequestId(session);
        Payment payment = new Payment(
                "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                requestId,
                String.valueOf(session.getAttribute("checkoutCustomerName")),
                String.valueOf(session.getAttribute("checkoutCustomerEmail")),
                parsePaymentMethod(request.getParameter("paymentMethod")),
                (Double) session.getAttribute("checkoutTotalAmount"));

        PaymentService paymentService = new PaymentService(getServletContext());
        Payment processedPayment = paymentService.processPayment(payment,
                request.getParameter("cardHolderName"),
                request.getParameter("cardNumber"),
                request.getParameter("expiryDate"),
                request.getParameter("cvv"));
        paymentService.savePayment(processedPayment);

        if (PaymentStatus.FAILED.equals(processedPayment.getPaymentStatus())) {
            request.setAttribute("error", "Payment failed. Check your demo payment details and try again.");
            request.setAttribute("payment", processedPayment);
            preparePaymentPage(request);
            request.getRequestDispatcher("/WEB-INF/views/payment.jsp").forward(request, response);
            return;
        }

        BookingRequest bookingRequest = new BookingRequest(
                requestId,
                String.valueOf(session.getAttribute("checkoutCustomerName")),
                String.valueOf(session.getAttribute("checkoutCustomerEmail")),
                String.valueOf(session.getAttribute("checkoutMovieLabel")),
                String.valueOf(session.getAttribute("selectedShowtimeId")),
                readSelectedSeats(session),
                (Double) session.getAttribute("checkoutTotalAmount"),
                BookingStatus.PENDING,
                LocalDateTime.now().toString(),
                "",
                "-");

        BookingService bookingService = new BookingService(getServletContext());
        BookingRequest queuedRequest = bookingService.enqueueBooking(bookingRequest);
        BookingRequest processedRequest = bookingService.processNext();
        String resultRequestId = processedRequest == null ? queuedRequest.getRequestId() : processedRequest.getRequestId();

        session.setAttribute("lastPaymentId", processedPayment.getPaymentId());
        clearCheckoutSession(session);
        response.sendRedirect(request.getContextPath() + "/booking-result?requestId=" + resultRequestId + "&paymentId=" + processedPayment.getPaymentId());
    }

    private boolean preparePaymentPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !hasCheckoutData(session)) {
            return false;
        }

        request.setAttribute("movieLabel", session.getAttribute("checkoutMovieLabel"));
        request.setAttribute("showtimeId", session.getAttribute("selectedShowtimeId"));
        request.setAttribute("selectedSeats", readSelectedSeats(session));
        request.setAttribute("customerName", session.getAttribute("checkoutCustomerName"));
        request.setAttribute("customerEmail", session.getAttribute("checkoutCustomerEmail"));
        request.setAttribute("totalAmount", session.getAttribute("checkoutTotalAmount"));
        request.setAttribute("paymentMethods", PaymentMethod.values());
        return true;
    }

    private boolean hasCheckoutData(HttpSession session) {
        return session.getAttribute("selectedShowtimeId") != null
                && !readSelectedSeats(session).isEmpty()
                && session.getAttribute("checkoutCustomerName") != null
                && session.getAttribute("checkoutCustomerEmail") != null
                && session.getAttribute("checkoutTotalAmount") instanceof Double;
    }

    private String getOrCreateBookingRequestId(HttpSession session) {
        Object existingRequestId = session.getAttribute("pendingBookingRequestId");
        if (existingRequestId != null && !existingRequestId.toString().trim().isEmpty()) {
            return existingRequestId.toString();
        }
        String requestId = "BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        session.setAttribute("pendingBookingRequestId", requestId);
        return requestId;
    }

    private ArrayList<String> readSelectedSeats(HttpSession session) {
        ArrayList<String> selectedSeats = new ArrayList<>();
        Object sessionSeats = session.getAttribute("selectedSeats");
        if (sessionSeats instanceof List<?>) {
            for (Object seat : (List<?>) sessionSeats) {
                if (seat != null && !seat.toString().trim().isEmpty()) {
                    selectedSeats.add(seat.toString().trim());
                }
            }
        } else if (sessionSeats instanceof String) {
            String[] parts = sessionSeats.toString().split(",");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    selectedSeats.add(part.trim());
                }
            }
        }
        return selectedSeats;
    }

    private PaymentMethod parsePaymentMethod(String value) {
        try {
            return PaymentMethod.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return PaymentMethod.CREDIT_CARD;
        }
    }

    private void clearCheckoutSession(HttpSession session) {
        session.removeAttribute("selectedShowtimeId");
        session.removeAttribute("selectedSeats");
        session.removeAttribute("pendingBookingRequestId");
        session.removeAttribute("checkoutMovieLabel");
        session.removeAttribute("checkoutCustomerName");
        session.removeAttribute("checkoutCustomerEmail");
        session.removeAttribute("checkoutTotalAmount");
    }
}
