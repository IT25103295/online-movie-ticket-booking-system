<%@ page import="com.moviebooking.model.Booking" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    Booking booking = (Booking) request.getAttribute("booking");
%>

<main class="ticket-page">
    <section class="container ticket-wrap">
        <div class="ticket-card">
            <div class="ticket-top">
                <div>
                    <span class="hero-kicker">E-Ticket</span>
                    <h1><%= booking.getMovieTitle() %></h1>
                </div>
                <span class="ticket-status"><%= booking.getStatus() %></span>
            </div>

            <div class="ticket-grid">
                <div><span>Booking ID</span><strong><%= booking.getBookingId() %></strong></div>
                <div><span>Request ID</span><strong><%= booking.getRequestId() %></strong></div>
                <div><span>Date</span><strong><%= booking.getShowtimeDate() %></strong></div>
                <div><span>Time</span><strong><%= booking.getShowtimeTime() %></strong></div>
                <div><span>Cinema Hall</span><strong><%= booking.getCinemaHall() %></strong></div>
                <div><span>Seats</span><strong><%= String.join(", ", booking.getSeats()) %></strong></div>
                <div><span>Customer</span><strong><%= booking.getCustomerName() %></strong></div>
                <div><span>Email</span><strong><%= booking.getCustomerEmail() %></strong></div>
                <div><span>Total Price</span><strong>LKR <%= String.format("%.2f", booking.getTotalPrice()) %></strong></div>
                <div><span>Confirmed At</span><strong><%= booking.getConfirmedAt() %></strong></div>
            </div>

            <div class="ticket-actions">
                <a class="btn btn-outline-light" href="${pageContext.request.contextPath}/my-bookings">Back</a>
                <button class="btn btn-danger" type="button" onclick="window.print()">Print Ticket</button>
            </div>
        </div>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
