<%@ page import="com.moviebooking.model.BookingRequest" %>
<%@ page import="com.moviebooking.model.BookingStatus" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    BookingRequest bookingRequest = (BookingRequest) request.getAttribute("bookingRequest");
    boolean confirmed = BookingStatus.CONFIRMED.equals(bookingRequest.getStatus());
%>

<main class="booking-page">
    <section class="container result-wrap">
        <div class="result-card <%= confirmed ? "confirmed" : "rejected" %>">
            <span class="hero-kicker">Booking Result</span>
            <h1><%= confirmed ? "Booking Confirmed" : "Booking Rejected" %></h1>
            <p>Request ID: <strong><%= bookingRequest.getRequestId() %></strong></p>

            <% if (confirmed) { %>
                <p>Your seats have been confirmed for showtime <strong><%= bookingRequest.getShowtimeId() %></strong>.</p>
            <% } else { %>
                <p>Reason: <strong><%= bookingRequest.getRejectionReason() %></strong></p>
            <% } %>

            <div class="summary-grid compact">
                <div class="summary-item"><span>Customer</span><strong><%= bookingRequest.getCustomerName() %></strong></div>
                <div class="summary-item"><span>Seats</span><strong><%= String.join(", ", bookingRequest.getSelectedSeats()) %></strong></div>
                <div class="summary-item"><span>Total</span><strong>LKR <%= String.format("%.2f", bookingRequest.getTotalPrice()) %></strong></div>
                <div class="summary-item"><span>Status</span><strong><%= bookingRequest.getStatus() %></strong></div>
            </div>

            <div class="checkout-actions">
                <a class="btn btn-outline-light" href="${pageContext.request.contextPath}/my-bookings">My Bookings</a>
                <a class="btn btn-danger" href="${pageContext.request.contextPath}/movies">Movies</a>
            </div>
        </div>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
